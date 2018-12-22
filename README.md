# spring-boot-starter-gae
Convenience tools to quickly work with Springboot and Google App Engine services.

Features:
* Authentication / Authorization via JWT
* Even-Driven processing
* Easy interface for Pub/Sub, Task Queues, Image transformations, and Cloud Storage
* Datastore CRUD operations

## Authentication / Authorization

#### Securing Endpoints
Annotate controllers with `@Secured` to make them private. Only requests with signed authorization token will be allowed.
```java
@Secured(VIEWER) // Only users with role "VIEWER" will be allowed access.
@GetMapping("/")
public ResponseEntity getUsers() {
  ...
}
```

#### Generating Authorization Token
```java
private final AuthTokenSigner tokenSigner;

...

  final String signedToken = tokenGenerator.signToken(verifiedUser)

```
#### Creating Custom Tokens
Simply create your own `TokenTranslator` bean.

```java
public class CustomTokenTranslator extends TokenTranslator<VerifiedUser> {

  @Override
  public JWTCreator.Builder toJwt(final VerifiedUser user, final JWTCreator.Builder jwtBuilder) {
    return jwtBuilder.withJWTId("123")
        .withSubject(user.getId())
        .withClaim("fn", user.getFirstname())
        .withClaim("ln", user.getLastname())
        .withArrayClaim("rl", user.getRoles().toArray(new String[]{}));
  }

  @Override
  public VerifiedUser decode(DecodedJWT decodedJWT) {
    final String id = decodedJWT.getSubject();
    final String firstname = decodedJWT.getClaim("fn").asString();
    final String lastname = decodedJWT.getClaim("ln").asString();
    final List<String> roles = decodedJWT.getClaim("rl").asList(String.class);

    final VerifiedUser user = new VerifiedUser(id, firstname, lastname, roles);
    return user;
  }
}
```

#### Changing the secret key used in signing tokens
Overwrite the property `bwee.jwt.secret.key`. Default key is set to "secret".

#### Getting the authenticated user
```java
@Autowire
private AuthUserContext context;

private boolean isLoggedIn() {
  return context.getAuthUser() != null;
}
```

## Event-Driven Processing
Some processes can be triggered with events. For instance, when a new user is saved, a few things may happen:
* a task can run to send an email verification.
* another task can run to scan the user's profile image.
* and yet another task can run to notify the user's friends to welcome him.

These events make use of Google Pub/Sub and Task Queues. When an event is triggered, data is sent to Pub/Sub topic. A task router listens to that topic, receives that data, and pushes it to the task queue. Finally, a task handler listens to the queue and processes the data.

#### Why do we route the data from Pub/Sub to Task Queue?
Task queues provide better control to how queued tasks are processed. For instance, we can control how fast they get processed, how often to retry failed tasks, and even allows you to kill a task. Pub/Sub on the other hand, if an error occurs, keeps retrying and floods your logs until the issue is fixed.

#### Usage
1) Annotate methods with `@PublishEvent(<pub_sub_topic>)` to start submitting event data to Pub/Sub

```java
@PublishEvent(USER_SAVED)
public User saveUser(User user) {
  ...
  return savedUser;  // savedUser gets sent to Google PubSub in JSON format.
}
```

2) Add a subscriber `/push/<task_name>` to that topic in Google Cloud Console. `PushToTaskRouterController` listens to PubSub and sends events to the task queue named `<task_name>` and with the path `/tasks/<task_name>`.

3) Make sure you have the `<task_name>` queue created.

4) Create a controller to handle requests coming from `/tasks/<task_name>`. Method type is `POST` and the payload is in JSON format.

## Datastore
1) Extend the `AbstractDao` to gain basic CRUD functions.
```java
public class UserDao extends AbstractDao<String, User, UserEntity> {

  // Convert entity to POJO
  @Override
  protected User toModel(final UserEntity entity) {
    return entity.toModel();
  }

  // Convert POJO to entity
  @Override
  protected UserEntity toEntity(final User user) {
    return new UserEntity(user);
  }
}
```

2) Create your entity, implementing the `BasicEntity`. If you need automatic timestampped fields like `createdTime` and `updatedTime`, implement the `TimestampedEntity` in your entity as well.
```java
@Cache(expirationSeconds = 60 * 60 * 24 * 7) // 1 week
@Entity(name="User")
public class UserEntity implements BasicEntity<String, UserEntity>, TimestampedEntity<UserEntity> {
```

3) Extend the `AbstractService`. All the entity stuff is hidden in the DAO layer.
```java
@Service
public class UserService extends AbstractService<String, User> {

  @Override
  @PublishEvent(TopicName.USER_SAVED) // Publish saved user to Google Pub/Sub
  public User save(final User staff) {
    return super.save(staff);
  }
}
```
