package entities;

public class GeoMongo {

	private float[] coordinates;

	public float[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(float[] coordinates) {
		this.coordinates = coordinates;
	}

	public float getLongitude() {
		return this.coordinates[0];

	}

	public float getLatitude() {
		return this.coordinates[1];
	}
}