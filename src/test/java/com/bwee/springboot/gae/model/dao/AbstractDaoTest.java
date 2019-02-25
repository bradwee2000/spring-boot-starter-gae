package com.bwee.springboot.gae.model.dao;

import com.bwee.springboot.gae.model.entity.BasicEntity;
import com.bwee.springboot.gae.model.entity.TimestampedEntity;
import com.bwee.springboot.gae.model.translator.LocalDateTimeTranslatorFactory;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Translate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author bradwee2000@gmail.com
 */
public class AbstractDaoTest extends DatastoreTest {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractDaoTest.class);

  private final LocalDateTime now = LocalDateTime.of(2018, 2, 19, 3, 22, 54);
  private final Clock clock = Clock.fixed(now.toInstant(ZonedDateTime.now().getOffset()), ZoneId.systemDefault());

  private DummyPojo dummyPojo1, dummyPojo2, dummyPojo3;
  private DummyDao dao;

  @BeforeClass
  public static void setup() {
    registerEntityClasses(DummyEntity.class);
  }

  @Before
  public void before() {
    this.dao = new DummyDao(clock);

    this.dummyPojo1 = new DummyPojo().setValue("Dummy 1");
    this.dummyPojo2 = new DummyPojo().setValue("Dummy 2");
    this.dummyPojo3 = new DummyPojo().setValue("Dummy 3");
  }

  @Test
  public void testSaveAndFind_shouldSaveToDbAndFindEqualObject() {
    final DummyPojo savedPojo = dao.save(dummyPojo1);
    assertThat(dao.findById(savedPojo.id)).hasValue(savedPojo);
  }

  @Test
  public void testSaveTimestampedEntity_shouldUpateTimestamps() {
    final DummyPojo savedPojo = dao.save(dummyPojo1);
    assertThat(savedPojo.updTime).isEqualTo(now);
    assertThat(savedPojo.createdTime).isEqualTo(now);
  }

  @Test
  public void testFindAll_shouldReturnAllPojos() {
    final DummyPojo savedPojo1 = dao.save(dummyPojo1);
    assertThat(dao.findAll()).containsExactlyInAnyOrder(savedPojo1);

    final DummyPojo savedPojo2 = dao.save(dummyPojo2);
    assertThat(dao.findAll()).containsExactlyInAnyOrder(savedPojo1, savedPojo2);
  }

  @Test
  public void testFindMultiple_shouldReturnMultiplePojosGivenTheirIds() {
    final DummyPojo savedPojo1 = dao.save(dummyPojo1);
    final DummyPojo savedPojo2 = dao.save(dummyPojo2);
    assertThat(dao.findByIds(savedPojo1.id, savedPojo2.id)).containsExactly(savedPojo1, savedPojo2);
  }

  @Test
  public void testSave_shouldReturnSavedPojo() {
    final DummyPojo savedPojo = dao.save(dummyPojo1);

    // Verify returned pojo values
    assertThat(savedPojo.id).isNotNull();
    assertThat(savedPojo.value).isEqualTo(dummyPojo1.value);
    assertThat(savedPojo.createdTime).isEqualTo(now);
    assertThat(savedPojo.updTime).isEqualTo(now);
  }

  @Test
  public void testFindOneWithUnknownId_shouldReturnEmpty() {
    assertThat(dao.findById(1l)).isEmpty();
    assertThat(dao.findById(2l)).isEmpty();
  }

  @Test
  public void testSaveMultiple_shouldSaveAllToDb() {
    final List<DummyPojo> savedPojos = dao.saveAll(dummyPojo1, dummyPojo2);

    // Verify returned pojo values
    assertThat(savedPojos).isNotNull();
    assertThat(savedPojos).hasSize(2);
    assertThat(savedPojos).extracting(p -> p.id).isNotNull();
    assertThat(savedPojos).extracting(p -> p.value).containsExactlyInAnyOrder(dummyPojo1.value, dummyPojo2.value);
  }

  @Test
  public void testFindMultiple_shouldReturnMultiplePostsGivenIds() {
    // Prepare and save pojos
    dummyPojo1.setId(1l);
    dummyPojo2.setId(2l);
    dao.saveAll(dummyPojo1, dummyPojo2);

    // Test fetching multiple including unknowns
    assertThat(dao.findByIds(1l, 2l, 3l, 4l)).hasSize(2);

    // Test fetching all unknowns
    assertThat(dao.findByIds(9l, 10l)).isEmpty();

    // Test fetching one known
    assertThat(dao.findByIds(1l)).extracting(p -> p.value).containsExactly(dummyPojo1.value);
    assertThat(dao.findByIds(2l)).extracting(p -> p.value).containsExactly(dummyPojo2.value);
  }

  @Test
  public void testDelete_shouldRemovePostFromDb() {
    // Prepare and save pojos
    dummyPojo1.setId(1l);
    dummyPojo2.setId(2l);
    dao.saveAll(dummyPojo1, dummyPojo2);

    // Delete one
    dao.delete(2l);

    // Verify only one is deleted
    assertThat(dao.findById(1l)).isNotEmpty();
    assertThat(dao.findById(2l)).isEmpty();

    // Delete another
    dao.delete(1l);

    // Verify all are deleted
    assertThat(dao.findByIds(1l, 2l)).isEmpty();
  }

  @Test
  public void testDeleteAll_shouldDeleteAll() {
    // Prepare and save pojos
    dummyPojo1.setId(1l);
    dummyPojo2.setId(2l);
    dao.saveAll(dummyPojo1, dummyPojo2);

    final List<Long> deletedKeys = dao.deleteAll();

    assertThat(dao.findAll()).isEmpty();
    assertThat(deletedKeys).containsExactlyInAnyOrder(1l, 2l);
  }

  @Test
  public void testDeleteAllGivenIds_shouldDeleteAllGivenIds() {
    // Prepare and save pojos
    dummyPojo1.setId(1l);
    dummyPojo2.setId(2l);
    dummyPojo3.setId(3l);
    dao.saveAll(dummyPojo1, dummyPojo2, dummyPojo3);

    final List<Long> deletedKeys = dao.deleteAll(1l, 3l);

    assertThat(dao.findAll()).containsExactly(dummyPojo2);
    assertThat(deletedKeys).containsExactlyInAnyOrder(1l, 3l);
  }

  /**
   * Dummy Dao
   */
  public static class DummyDao extends AbstractDao<Long, DummyPojo, DummyEntity> {

    public DummyDao(Clock clock) {
      super(clock, DummyEntity.class);
    }

    @Override
    protected Key<DummyEntity> key(Long id) {
      return Key.create(DummyEntity.class, id);
    }

    @Override
    protected DummyPojo toModel(DummyEntity entity) {
      return new DummyPojo(entity);
    }

    @Override
    protected DummyEntity toEntity(DummyPojo pojo) {
      return new DummyEntity(pojo);
    }
  }

  /**
   * Dummy Pojo
   */
  public static class DummyPojo {
    private Long id;
    private String value;
    private LocalDateTime createdTime;
    private LocalDateTime updTime;

    public DummyPojo() {}

    public DummyPojo(DummyEntity entity) {
      this.id = entity.id;
      this.value = entity.value;
      this.createdTime = entity.getCreatedTime();
      this.updTime = entity.getUpdatedTime();
    }

    public DummyPojo setId(Long id) {
      this.id = id;
      return this;
    }

    public DummyPojo setValue(String value) {
      this.value = value;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      DummyPojo dummyPojo = (DummyPojo) o;
      return Objects.equal(id, dummyPojo.id) &&
              Objects.equal(value, dummyPojo.value);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(id, value);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
              .add("id", id)
              .add("value", value)
              .add("createdTime", createdTime)
              .add("updTime", updTime)
              .toString();
    }
  }

  /**
   * Dummy Entity
   */
  @Entity
  public static class DummyEntity implements BasicEntity<Long, DummyEntity>, TimestampedEntity<DummyEntity> {
    @Id
    private Long id;

    private String value;

    @Translate(LocalDateTimeTranslatorFactory.class)
    private LocalDateTime createdTime;

    @Translate(LocalDateTimeTranslatorFactory.class)
    private LocalDateTime updatedTime;

    public DummyEntity() {}

    public DummyEntity(DummyPojo pojo) {
      this.id = pojo.id;
      this.value = pojo.value;
    }

    @Override
    public Long getId() {
      return id;
    }

    @Override
    public DummyEntity setId(Long id) {
      this.id = id;
      return this;
    }

    @Override
    public LocalDateTime getCreatedTime() {
      return createdTime;
    }

    @Override
    public DummyEntity setCreatedTime(LocalDateTime createdTime) {
      this.createdTime = createdTime;
      return this;
    }

    @Override
    public LocalDateTime getUpdatedTime() {
      return updatedTime;
    }

    @Override
    public DummyEntity setUpdatedTime(LocalDateTime updatedTime) {
      this.updatedTime = updatedTime;
      return this;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
              .add("id", id)
              .add("value", value)
              .add("createdTime", createdTime)
              .add("updatedTime", updatedTime)
              .toString();
    }
  }

}