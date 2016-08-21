package entities;

public class Venue {

	private String name;
	private String address;
	private float latitude;
	private float longitude;
	
	public Venue (String name, String address)
	{
		this.name = name;
		this.address = address;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

}
