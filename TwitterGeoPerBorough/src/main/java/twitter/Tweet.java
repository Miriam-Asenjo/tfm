/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package twitter;

import org.apache.avro.specific.SpecificData;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class Tweet extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -7251817501519344799L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Tweet\",\"namespace\":\"twitter\",\"fields\":[{\"name\":\"created_at\",\"type\":\"string\"},{\"name\":\"text\",\"type\":\"string\"},{\"name\":\"source\",\"type\":\"string\"},{\"name\":\"user\",\"type\":{\"type\":\"record\",\"name\":\"UserDetails\",\"fields\":[{\"name\":\"followers_count\",\"type\":\"int\"},{\"name\":\"friends_count\",\"type\":\"int\"},{\"name\":\"listed_count\",\"type\":\"int\"},{\"name\":\"favourites_count\",\"type\":\"int\"},{\"name\":\"statuses_count\",\"type\":\"int\"},{\"name\":\"created_at\",\"type\":[\"null\",\"string\"]},{\"name\":\"utc_offset\",\"type\":[\"null\",\"int\"],\"default\":0},{\"name\":\"time_zone\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"geo_enabled\",\"type\":\"boolean\"},{\"name\":\"lang\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"contributors_enabled\",\"type\":\"boolean\"},{\"name\":\"is_translator\",\"type\":\"boolean\"},{\"name\":\"profile_background_color\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"profile_background_image_url\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"profile_background_image_url_https\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"profile_background_tile\",\"type\":\"boolean\"},{\"name\":\"profile_link_color\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"profile_sidebar_border_color\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"profile_sidebar_fill_color\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"profile_text_color\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"profile_use_background_image\",\"type\":\"boolean\"},{\"name\":\"profile_image_url\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"profile_banner_url\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"default_profile\",\"type\":\"boolean\"},{\"name\":\"default_profile_image\",\"type\":\"boolean\"},{\"name\":\"following\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"follow_request_sent\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"notifications\",\"type\":[\"null\",\"string\"],\"default\":null}]}},{\"name\":\"coordinates\",\"type\":{\"type\":\"record\",\"name\":\"CoordinatesDetails\",\"fields\":[{\"name\":\"type\",\"type\":\"string\"},{\"name\":\"coordinates\",\"type\":{\"type\":\"array\",\"items\":\"float\"}}]}},{\"name\":\"place\",\"type\":{\"type\":\"record\",\"name\":\"PlaceDetails\",\"fields\":[{\"name\":\"id\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"url\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"place_type\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"name\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"full_name\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"country_code\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"country\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"bounding_box\",\"type\":{\"type\":\"record\",\"name\":\"BoundingBox\",\"fields\":[{\"name\":\"type\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"coordinates\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"array\",\"items\":{\"type\":\"array\",\"items\":\"float\"}}}}]}},{\"name\":\"attributes\",\"type\":{\"type\":\"record\",\"name\":\"AttributesDetails\",\"fields\":[]}}]}},{\"name\":\"timestamp_ms\",\"type\":\"string\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.CharSequence created_at;
  @Deprecated public java.lang.CharSequence text;
  @Deprecated public java.lang.CharSequence source;
  @Deprecated public twitter.UserDetails user;
  @Deprecated public twitter.CoordinatesDetails coordinates;
  @Deprecated public twitter.PlaceDetails place;
  @Deprecated public java.lang.CharSequence timestamp_ms;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Tweet() {}

  /**
   * All-args constructor.
   * @param created_at The new value for created_at
   * @param text The new value for text
   * @param source The new value for source
   * @param user The new value for user
   * @param coordinates The new value for coordinates
   * @param place The new value for place
   * @param timestamp_ms The new value for timestamp_ms
   */
  public Tweet(java.lang.CharSequence created_at, java.lang.CharSequence text, java.lang.CharSequence source, twitter.UserDetails user, twitter.CoordinatesDetails coordinates, twitter.PlaceDetails place, java.lang.CharSequence timestamp_ms) {
    this.created_at = created_at;
    this.text = text;
    this.source = source;
    this.user = user;
    this.coordinates = coordinates;
    this.place = place;
    this.timestamp_ms = timestamp_ms;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return created_at;
    case 1: return text;
    case 2: return source;
    case 3: return user;
    case 4: return coordinates;
    case 5: return place;
    case 6: return timestamp_ms;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: created_at = (java.lang.CharSequence)value$; break;
    case 1: text = (java.lang.CharSequence)value$; break;
    case 2: source = (java.lang.CharSequence)value$; break;
    case 3: user = (twitter.UserDetails)value$; break;
    case 4: coordinates = (twitter.CoordinatesDetails)value$; break;
    case 5: place = (twitter.PlaceDetails)value$; break;
    case 6: timestamp_ms = (java.lang.CharSequence)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'created_at' field.
   * @return The value of the 'created_at' field.
   */
  public java.lang.CharSequence getCreatedAt() {
    return created_at;
  }

  /**
   * Sets the value of the 'created_at' field.
   * @param value the value to set.
   */
  public void setCreatedAt(java.lang.CharSequence value) {
    this.created_at = value;
  }

  /**
   * Gets the value of the 'text' field.
   * @return The value of the 'text' field.
   */
  public java.lang.CharSequence getText() {
    return text;
  }

  /**
   * Sets the value of the 'text' field.
   * @param value the value to set.
   */
  public void setText(java.lang.CharSequence value) {
    this.text = value;
  }

  /**
   * Gets the value of the 'source' field.
   * @return The value of the 'source' field.
   */
  public java.lang.CharSequence getSource() {
    return source;
  }

  /**
   * Sets the value of the 'source' field.
   * @param value the value to set.
   */
  public void setSource(java.lang.CharSequence value) {
    this.source = value;
  }

  /**
   * Gets the value of the 'user' field.
   * @return The value of the 'user' field.
   */
  public twitter.UserDetails getUser() {
    return user;
  }

  /**
   * Sets the value of the 'user' field.
   * @param value the value to set.
   */
  public void setUser(twitter.UserDetails value) {
    this.user = value;
  }

  /**
   * Gets the value of the 'coordinates' field.
   * @return The value of the 'coordinates' field.
   */
  public twitter.CoordinatesDetails getCoordinates() {
    return coordinates;
  }

  /**
   * Sets the value of the 'coordinates' field.
   * @param value the value to set.
   */
  public void setCoordinates(twitter.CoordinatesDetails value) {
    this.coordinates = value;
  }

  /**
   * Gets the value of the 'place' field.
   * @return The value of the 'place' field.
   */
  public twitter.PlaceDetails getPlace() {
    return place;
  }

  /**
   * Sets the value of the 'place' field.
   * @param value the value to set.
   */
  public void setPlace(twitter.PlaceDetails value) {
    this.place = value;
  }

  /**
   * Gets the value of the 'timestamp_ms' field.
   * @return The value of the 'timestamp_ms' field.
   */
  public java.lang.CharSequence getTimestampMs() {
    return timestamp_ms;
  }

  /**
   * Sets the value of the 'timestamp_ms' field.
   * @param value the value to set.
   */
  public void setTimestampMs(java.lang.CharSequence value) {
    this.timestamp_ms = value;
  }

  /**
   * Creates a new Tweet RecordBuilder.
   * @return A new Tweet RecordBuilder
   */
  public static twitter.Tweet.Builder newBuilder() {
    return new twitter.Tweet.Builder();
  }

  /**
   * Creates a new Tweet RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Tweet RecordBuilder
   */
  public static twitter.Tweet.Builder newBuilder(twitter.Tweet.Builder other) {
    return new twitter.Tweet.Builder(other);
  }

  /**
   * Creates a new Tweet RecordBuilder by copying an existing Tweet instance.
   * @param other The existing instance to copy.
   * @return A new Tweet RecordBuilder
   */
  public static twitter.Tweet.Builder newBuilder(twitter.Tweet other) {
    return new twitter.Tweet.Builder(other);
  }

  /**
   * RecordBuilder for Tweet instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Tweet>
    implements org.apache.avro.data.RecordBuilder<Tweet> {

    private java.lang.CharSequence created_at;
    private java.lang.CharSequence text;
    private java.lang.CharSequence source;
    private twitter.UserDetails user;
    private twitter.UserDetails.Builder userBuilder;
    private twitter.CoordinatesDetails coordinates;
    private twitter.CoordinatesDetails.Builder coordinatesBuilder;
    private twitter.PlaceDetails place;
    private twitter.PlaceDetails.Builder placeBuilder;
    private java.lang.CharSequence timestamp_ms;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(twitter.Tweet.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.created_at)) {
        this.created_at = data().deepCopy(fields()[0].schema(), other.created_at);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.text)) {
        this.text = data().deepCopy(fields()[1].schema(), other.text);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.source)) {
        this.source = data().deepCopy(fields()[2].schema(), other.source);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.user)) {
        this.user = data().deepCopy(fields()[3].schema(), other.user);
        fieldSetFlags()[3] = true;
      }
      if (other.hasUserBuilder()) {
        this.userBuilder = twitter.UserDetails.newBuilder(other.getUserBuilder());
      }
      if (isValidValue(fields()[4], other.coordinates)) {
        this.coordinates = data().deepCopy(fields()[4].schema(), other.coordinates);
        fieldSetFlags()[4] = true;
      }
      if (other.hasCoordinatesBuilder()) {
        this.coordinatesBuilder = twitter.CoordinatesDetails.newBuilder(other.getCoordinatesBuilder());
      }
      if (isValidValue(fields()[5], other.place)) {
        this.place = data().deepCopy(fields()[5].schema(), other.place);
        fieldSetFlags()[5] = true;
      }
      if (other.hasPlaceBuilder()) {
        this.placeBuilder = twitter.PlaceDetails.newBuilder(other.getPlaceBuilder());
      }
      if (isValidValue(fields()[6], other.timestamp_ms)) {
        this.timestamp_ms = data().deepCopy(fields()[6].schema(), other.timestamp_ms);
        fieldSetFlags()[6] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing Tweet instance
     * @param other The existing instance to copy.
     */
    private Builder(twitter.Tweet other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.created_at)) {
        this.created_at = data().deepCopy(fields()[0].schema(), other.created_at);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.text)) {
        this.text = data().deepCopy(fields()[1].schema(), other.text);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.source)) {
        this.source = data().deepCopy(fields()[2].schema(), other.source);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.user)) {
        this.user = data().deepCopy(fields()[3].schema(), other.user);
        fieldSetFlags()[3] = true;
      }
      this.userBuilder = null;
      if (isValidValue(fields()[4], other.coordinates)) {
        this.coordinates = data().deepCopy(fields()[4].schema(), other.coordinates);
        fieldSetFlags()[4] = true;
      }
      this.coordinatesBuilder = null;
      if (isValidValue(fields()[5], other.place)) {
        this.place = data().deepCopy(fields()[5].schema(), other.place);
        fieldSetFlags()[5] = true;
      }
      this.placeBuilder = null;
      if (isValidValue(fields()[6], other.timestamp_ms)) {
        this.timestamp_ms = data().deepCopy(fields()[6].schema(), other.timestamp_ms);
        fieldSetFlags()[6] = true;
      }
    }

    /**
      * Gets the value of the 'created_at' field.
      * @return The value.
      */
    public java.lang.CharSequence getCreatedAt() {
      return created_at;
    }

    /**
      * Sets the value of the 'created_at' field.
      * @param value The value of 'created_at'.
      * @return This builder.
      */
    public twitter.Tweet.Builder setCreatedAt(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.created_at = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'created_at' field has been set.
      * @return True if the 'created_at' field has been set, false otherwise.
      */
    public boolean hasCreatedAt() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'created_at' field.
      * @return This builder.
      */
    public twitter.Tweet.Builder clearCreatedAt() {
      created_at = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'text' field.
      * @return The value.
      */
    public java.lang.CharSequence getText() {
      return text;
    }

    /**
      * Sets the value of the 'text' field.
      * @param value The value of 'text'.
      * @return This builder.
      */
    public twitter.Tweet.Builder setText(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.text = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'text' field has been set.
      * @return True if the 'text' field has been set, false otherwise.
      */
    public boolean hasText() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'text' field.
      * @return This builder.
      */
    public twitter.Tweet.Builder clearText() {
      text = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'source' field.
      * @return The value.
      */
    public java.lang.CharSequence getSource() {
      return source;
    }

    /**
      * Sets the value of the 'source' field.
      * @param value The value of 'source'.
      * @return This builder.
      */
    public twitter.Tweet.Builder setSource(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.source = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'source' field has been set.
      * @return True if the 'source' field has been set, false otherwise.
      */
    public boolean hasSource() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'source' field.
      * @return This builder.
      */
    public twitter.Tweet.Builder clearSource() {
      source = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'user' field.
      * @return The value.
      */
    public twitter.UserDetails getUser() {
      return user;
    }

    /**
      * Sets the value of the 'user' field.
      * @param value The value of 'user'.
      * @return This builder.
      */
    public twitter.Tweet.Builder setUser(twitter.UserDetails value) {
      validate(fields()[3], value);
      this.userBuilder = null;
      this.user = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'user' field has been set.
      * @return True if the 'user' field has been set, false otherwise.
      */
    public boolean hasUser() {
      return fieldSetFlags()[3];
    }

    /**
     * Gets the Builder instance for the 'user' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public twitter.UserDetails.Builder getUserBuilder() {
      if (userBuilder == null) {
        if (hasUser()) {
          setUserBuilder(twitter.UserDetails.newBuilder(user));
        } else {
          setUserBuilder(twitter.UserDetails.newBuilder());
        }
      }
      return userBuilder;
    }

    /**
     * Sets the Builder instance for the 'user' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */
    public twitter.Tweet.Builder setUserBuilder(twitter.UserDetails.Builder value) {
      clearUser();
      userBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'user' field has an active Builder instance
     * @return True if the 'user' field has an active Builder instance
     */
    public boolean hasUserBuilder() {
      return userBuilder != null;
    }

    /**
      * Clears the value of the 'user' field.
      * @return This builder.
      */
    public twitter.Tweet.Builder clearUser() {
      user = null;
      userBuilder = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'coordinates' field.
      * @return The value.
      */
    public twitter.CoordinatesDetails getCoordinates() {
      return coordinates;
    }

    /**
      * Sets the value of the 'coordinates' field.
      * @param value The value of 'coordinates'.
      * @return This builder.
      */
    public twitter.Tweet.Builder setCoordinates(twitter.CoordinatesDetails value) {
      validate(fields()[4], value);
      this.coordinatesBuilder = null;
      this.coordinates = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'coordinates' field has been set.
      * @return True if the 'coordinates' field has been set, false otherwise.
      */
    public boolean hasCoordinates() {
      return fieldSetFlags()[4];
    }

    /**
     * Gets the Builder instance for the 'coordinates' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public twitter.CoordinatesDetails.Builder getCoordinatesBuilder() {
      if (coordinatesBuilder == null) {
        if (hasCoordinates()) {
          setCoordinatesBuilder(twitter.CoordinatesDetails.newBuilder(coordinates));
        } else {
          setCoordinatesBuilder(twitter.CoordinatesDetails.newBuilder());
        }
      }
      return coordinatesBuilder;
    }

    /**
     * Sets the Builder instance for the 'coordinates' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */
    public twitter.Tweet.Builder setCoordinatesBuilder(twitter.CoordinatesDetails.Builder value) {
      clearCoordinates();
      coordinatesBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'coordinates' field has an active Builder instance
     * @return True if the 'coordinates' field has an active Builder instance
     */
    public boolean hasCoordinatesBuilder() {
      return coordinatesBuilder != null;
    }

    /**
      * Clears the value of the 'coordinates' field.
      * @return This builder.
      */
    public twitter.Tweet.Builder clearCoordinates() {
      coordinates = null;
      coordinatesBuilder = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'place' field.
      * @return The value.
      */
    public twitter.PlaceDetails getPlace() {
      return place;
    }

    /**
      * Sets the value of the 'place' field.
      * @param value The value of 'place'.
      * @return This builder.
      */
    public twitter.Tweet.Builder setPlace(twitter.PlaceDetails value) {
      validate(fields()[5], value);
      this.placeBuilder = null;
      this.place = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'place' field has been set.
      * @return True if the 'place' field has been set, false otherwise.
      */
    public boolean hasPlace() {
      return fieldSetFlags()[5];
    }

    /**
     * Gets the Builder instance for the 'place' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public twitter.PlaceDetails.Builder getPlaceBuilder() {
      if (placeBuilder == null) {
        if (hasPlace()) {
          setPlaceBuilder(twitter.PlaceDetails.newBuilder(place));
        } else {
          setPlaceBuilder(twitter.PlaceDetails.newBuilder());
        }
      }
      return placeBuilder;
    }

    /**
     * Sets the Builder instance for the 'place' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */
    public twitter.Tweet.Builder setPlaceBuilder(twitter.PlaceDetails.Builder value) {
      clearPlace();
      placeBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'place' field has an active Builder instance
     * @return True if the 'place' field has an active Builder instance
     */
    public boolean hasPlaceBuilder() {
      return placeBuilder != null;
    }

    /**
      * Clears the value of the 'place' field.
      * @return This builder.
      */
    public twitter.Tweet.Builder clearPlace() {
      place = null;
      placeBuilder = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
      * Gets the value of the 'timestamp_ms' field.
      * @return The value.
      */
    public java.lang.CharSequence getTimestampMs() {
      return timestamp_ms;
    }

    /**
      * Sets the value of the 'timestamp_ms' field.
      * @param value The value of 'timestamp_ms'.
      * @return This builder.
      */
    public twitter.Tweet.Builder setTimestampMs(java.lang.CharSequence value) {
      validate(fields()[6], value);
      this.timestamp_ms = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'timestamp_ms' field has been set.
      * @return True if the 'timestamp_ms' field has been set, false otherwise.
      */
    public boolean hasTimestampMs() {
      return fieldSetFlags()[6];
    }


    /**
      * Clears the value of the 'timestamp_ms' field.
      * @return This builder.
      */
    public twitter.Tweet.Builder clearTimestampMs() {
      timestamp_ms = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    @Override
    public Tweet build() {
      try {
        Tweet record = new Tweet();
        record.created_at = fieldSetFlags()[0] ? this.created_at : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.text = fieldSetFlags()[1] ? this.text : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.source = fieldSetFlags()[2] ? this.source : (java.lang.CharSequence) defaultValue(fields()[2]);
        if (userBuilder != null) {
          record.user = this.userBuilder.build();
        } else {
          record.user = fieldSetFlags()[3] ? this.user : (twitter.UserDetails) defaultValue(fields()[3]);
        }
        if (coordinatesBuilder != null) {
          record.coordinates = this.coordinatesBuilder.build();
        } else {
          record.coordinates = fieldSetFlags()[4] ? this.coordinates : (twitter.CoordinatesDetails) defaultValue(fields()[4]);
        }
        if (placeBuilder != null) {
          record.place = this.placeBuilder.build();
        } else {
          record.place = fieldSetFlags()[5] ? this.place : (twitter.PlaceDetails) defaultValue(fields()[5]);
        }
        record.timestamp_ms = fieldSetFlags()[6] ? this.timestamp_ms : (java.lang.CharSequence) defaultValue(fields()[6]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  private static final org.apache.avro.io.DatumWriter
    WRITER$ = new org.apache.avro.specific.SpecificDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  private static final org.apache.avro.io.DatumReader
    READER$ = new org.apache.avro.specific.SpecificDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
