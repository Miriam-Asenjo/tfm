package entities;

import google.places.Geometry;

public class VenueLocation
{
	private Geometry geometry;

	public Geometry getGeometry()
	{
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	
}