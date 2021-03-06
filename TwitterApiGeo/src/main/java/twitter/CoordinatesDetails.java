/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package twitter;

import org.apache.avro.specific.SpecificData;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class CoordinatesDetails extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -1652924730433030340L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"CoordinatesDetails\",\"namespace\":\"twitter\",\"fields\":[{\"name\":\"type\",\"type\":\"string\"},{\"name\":\"coordinates\",\"type\":{\"type\":\"array\",\"items\":\"float\"}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.CharSequence type;
  @Deprecated public java.util.List<java.lang.Float> coordinates;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public CoordinatesDetails() {}

  /**
   * All-args constructor.
   * @param type The new value for type
   * @param coordinates The new value for coordinates
   */
  public CoordinatesDetails(java.lang.CharSequence type, java.util.List<java.lang.Float> coordinates) {
    this.type = type;
    this.coordinates = coordinates;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return type;
    case 1: return coordinates;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: type = (java.lang.CharSequence)value$; break;
    case 1: coordinates = (java.util.List<java.lang.Float>)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'type' field.
   * @return The value of the 'type' field.
   */
  public java.lang.CharSequence getType() {
    return type;
  }

  /**
   * Sets the value of the 'type' field.
   * @param value the value to set.
   */
  public void setType(java.lang.CharSequence value) {
    this.type = value;
  }

  /**
   * Gets the value of the 'coordinates' field.
   * @return The value of the 'coordinates' field.
   */
  public java.util.List<java.lang.Float> getCoordinates() {
    return coordinates;
  }

  /**
   * Sets the value of the 'coordinates' field.
   * @param value the value to set.
   */
  public void setCoordinates(java.util.List<java.lang.Float> value) {
    this.coordinates = value;
  }

  /**
   * Creates a new CoordinatesDetails RecordBuilder.
   * @return A new CoordinatesDetails RecordBuilder
   */
  public static twitter.CoordinatesDetails.Builder newBuilder() {
    return new twitter.CoordinatesDetails.Builder();
  }

  /**
   * Creates a new CoordinatesDetails RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new CoordinatesDetails RecordBuilder
   */
  public static twitter.CoordinatesDetails.Builder newBuilder(twitter.CoordinatesDetails.Builder other) {
    return new twitter.CoordinatesDetails.Builder(other);
  }

  /**
   * Creates a new CoordinatesDetails RecordBuilder by copying an existing CoordinatesDetails instance.
   * @param other The existing instance to copy.
   * @return A new CoordinatesDetails RecordBuilder
   */
  public static twitter.CoordinatesDetails.Builder newBuilder(twitter.CoordinatesDetails other) {
    return new twitter.CoordinatesDetails.Builder(other);
  }

  /**
   * RecordBuilder for CoordinatesDetails instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<CoordinatesDetails>
    implements org.apache.avro.data.RecordBuilder<CoordinatesDetails> {

    private java.lang.CharSequence type;
    private java.util.List<java.lang.Float> coordinates;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(twitter.CoordinatesDetails.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.type)) {
        this.type = data().deepCopy(fields()[0].schema(), other.type);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.coordinates)) {
        this.coordinates = data().deepCopy(fields()[1].schema(), other.coordinates);
        fieldSetFlags()[1] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing CoordinatesDetails instance
     * @param other The existing instance to copy.
     */
    private Builder(twitter.CoordinatesDetails other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.type)) {
        this.type = data().deepCopy(fields()[0].schema(), other.type);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.coordinates)) {
        this.coordinates = data().deepCopy(fields()[1].schema(), other.coordinates);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'type' field.
      * @return The value.
      */
    public java.lang.CharSequence getType() {
      return type;
    }

    /**
      * Sets the value of the 'type' field.
      * @param value The value of 'type'.
      * @return This builder.
      */
    public twitter.CoordinatesDetails.Builder setType(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.type = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'type' field has been set.
      * @return True if the 'type' field has been set, false otherwise.
      */
    public boolean hasType() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'type' field.
      * @return This builder.
      */
    public twitter.CoordinatesDetails.Builder clearType() {
      type = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'coordinates' field.
      * @return The value.
      */
    public java.util.List<java.lang.Float> getCoordinates() {
      return coordinates;
    }

    /**
      * Sets the value of the 'coordinates' field.
      * @param value The value of 'coordinates'.
      * @return This builder.
      */
    public twitter.CoordinatesDetails.Builder setCoordinates(java.util.List<java.lang.Float> value) {
      validate(fields()[1], value);
      this.coordinates = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'coordinates' field has been set.
      * @return True if the 'coordinates' field has been set, false otherwise.
      */
    public boolean hasCoordinates() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'coordinates' field.
      * @return This builder.
      */
    public twitter.CoordinatesDetails.Builder clearCoordinates() {
      coordinates = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    public CoordinatesDetails build() {
      try {
        CoordinatesDetails record = new CoordinatesDetails();
        record.type = fieldSetFlags()[0] ? this.type : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.coordinates = fieldSetFlags()[1] ? this.coordinates : (java.util.List<java.lang.Float>) defaultValue(fields()[1]);
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
