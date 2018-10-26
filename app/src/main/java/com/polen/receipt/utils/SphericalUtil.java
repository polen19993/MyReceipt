package com.polen.receipt.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class SphericalUtil {
	static final double EARTH_RADIUS = 6371009;

	/**
	 * Returns hav() of distance from (lat1, lng1) to (lat2, lng2) on the unit sphere.
	 */
	static double havDistance(double lat1, double lat2, double dLng) {
		return hav(lat1 - lat2) + hav(dLng) * Math.cos(lat1) * Math.cos(lat2);
	}

	/**
	 * Returns haversine(angle-in-radians).
	 * hav(x) == (1 - cos(x)) / 2 == sin(x / 2)^2.
	 */
	static double hav(double x) {
		double sinHalf = Math.sin(x * 0.5);
		return sinHalf * sinHalf;
	}

	/**
	 * Computes inverse haversine. Has good numerical stability around 0.
	 * arcHav(x) == acos(1 - 2 * x) == 2 * asin(sqrt(x)).
	 * The argument must be in [0, 1], and the result is positive.
	 */
	static double arcHav(double x) {
		return 2 * Math.asin(Math.sqrt(x));
	}

	private SphericalUtil() {
	}

	/**
	 * Returns the heading from one LatLng to another LatLng. Headings are
	 * expressed in degrees clockwise from North within the range [-180,180).
	 *
	 * @return The heading in degrees clockwise from north.
	 */
	public static double computeHeading(LatLng from, LatLng to) {
		// http://williams.best.vwh.net/avform.htm#Crs
		double fromLat = Math.toRadians(from.latitude);
		double fromLng = Math.toRadians(from.longitude);
		double toLat = Math.toRadians(to.latitude);
		double toLng = Math.toRadians(to.longitude);
		double dLng = toLng - fromLng;
		double heading = Math.atan2(Math.sin(dLng) * Math.cos(toLat), Math.cos(fromLat) * Math.sin(toLat) - Math.sin(fromLat) * Math.cos(toLat) * Math.cos(dLng));
		return wrap(Math.toDegrees(heading), -180, 180);
	}

	/**
	 * Returns the LatLng resulting from moving a distance from an origin
	 * in the specified heading (expressed in degrees clockwise from north).
	 *
	 * @param from     The LatLng from which to start.
	 * @param distance The distance to travel.
	 * @param heading  The heading in degrees clockwise from north.
	 */
	public static LatLng computeOffset(LatLng from, double distance, double heading) {
		distance /= EARTH_RADIUS;
		heading = Math.toRadians(heading);
		// http://williams.best.vwh.net/avform.htm#LL
		double fromLat = Math.toRadians(from.latitude);
		double fromLng = Math.toRadians(from.longitude);
		double cosDistance = Math.cos(distance);
		double sinDistance = Math.sin(distance);
		double sinFromLat = Math.sin(fromLat);
		double cosFromLat = Math.cos(fromLat);
		double sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat * Math.cos(heading);
		double dLng = Math.atan2(sinDistance * cosFromLat * Math.sin(heading), cosDistance - sinFromLat * sinLat);
		return new LatLng(Math.toDegrees(Math.asin(sinLat)), Math.toDegrees(fromLng + dLng));
	}

	/**
	 * Returns the location of origin when provided with a LatLng destination,
	 * meters travelled and original heading. Headings are expressed in degrees
	 * clockwise from North. This function returns null when no solution is
	 * available.
	 *
	 * @param to       The destination LatLng.
	 * @param distance The distance travelled, in meters.
	 * @param heading  The heading in degrees clockwise from north.
	 */
	public static LatLng computeOffsetOrigin(LatLng to, double distance, double heading) {
		heading = Math.toRadians(heading);
		distance /= EARTH_RADIUS;
		// http://lists.maptools.org/pipermail/proj/2008-October/003939.html
		double n1 = Math.cos(distance);
		double n2 = Math.sin(distance) * Math.cos(heading);
		double n3 = Math.sin(distance) * Math.sin(heading);
		double n4 = Math.sin(Math.toRadians(to.latitude));
		// There are two solutions for b. b = n2 * n4 +/- sqrt(), one solution results
		// in the latitude outside the [-90, 90] range. We first try one solution and
		// back off to the other if we are outside that range.
		double n12 = n1 * n1;
		double discriminant = n2 * n2 * n12 + n12 * n12 - n12 * n4 * n4;
		if (discriminant < 0) {
			// No real solution which would make sense in LatLng-space.
			return null;
		}
		double b = n2 * n4 + Math.sqrt(discriminant);
		b /= n1 * n1 + n2 * n2;
		double a = (n4 - n2 * b) / n1;
		double fromLatRadians = Math.atan2(a, b);
		if (fromLatRadians < -Math.PI / 2 || fromLatRadians > Math.PI / 2) {
			b = n2 * n4 - Math.sqrt(discriminant);
			b /= n1 * n1 + n2 * n2;
			fromLatRadians = Math.atan2(a, b);
		}
		if (fromLatRadians < -Math.PI / 2 || fromLatRadians > Math.PI / 2) {
			// No solution which would make sense in LatLng-space.
			return null;
		}
		double fromLngRadians = Math.toRadians(to.longitude) - Math.atan2(n3, n1 * Math.cos(fromLatRadians) - n2 * Math.sin(fromLatRadians));
		return new LatLng(Math.toDegrees(fromLatRadians), Math.toDegrees(fromLngRadians));
	}

	/**
	 * Returns the LatLng which lies the given fraction of the way between the
	 * origin LatLng and the destination LatLng.
	 *
	 * @param from     The LatLng from which to start.
	 * @param to       The LatLng toward which to travel.
	 * @param fraction A fraction of the distance to travel.
	 * @return The interpolated LatLng.
	 */
	public static LatLng interpolate(LatLng from, LatLng to, double fraction) {
		// http://en.wikipedia.org/wiki/Slerp
		double fromLat = Math.toRadians(from.latitude);
		double fromLng = Math.toRadians(from.longitude);
		double toLat = Math.toRadians(to.latitude);
		double toLng = Math.toRadians(to.longitude);
		double cosFromLat = Math.cos(fromLat);
		double cosToLat = Math.cos(toLat);

		// Computes Spherical interpolation coefficients.
		double angle = computeAngleBetween(from, to);
		double sinAngle = Math.sin(angle);
		if (sinAngle < 1E-6) {
			return from;
		}
		double a = Math.sin((1 - fraction) * angle) / sinAngle;
		double b = Math.sin(fraction * angle) / sinAngle;

		// Converts from polar to vector and interpolate.
		double x = a * cosFromLat * Math.cos(fromLng) + b * cosToLat * Math.cos(toLng);
		double y = a * cosFromLat * Math.sin(fromLng) + b * cosToLat * Math.sin(toLng);
		double z = a * Math.sin(fromLat) + b * Math.sin(toLat);

		// Converts interpolated vector back to polar.
		double lat = Math.atan2(z, Math.sqrt(x * x + y * y));
		double lng = Math.atan2(y, x);
		return new LatLng(Math.toDegrees(lat), Math.toDegrees(lng));
	}

	/**
	 * Returns distance on the unit sphere; the arguments are in radians.
	 */
	private static double distanceRadians(double lat1, double lng1, double lat2, double lng2) {
		return arcHav(havDistance(lat1, lat2, lng1 - lng2));
	}

	/**
	 * Returns the angle between two LatLngs, in radians. This is the same as the distance
	 * on the unit sphere.
	 */
	static double computeAngleBetween(LatLng from, LatLng to) {
		return distanceRadians(Math.toRadians(from.latitude), Math.toRadians(from.longitude), Math.toRadians(to.latitude), Math.toRadians(to.longitude));
	}

	/**
	 * Returns the distance between two LatLngs, in meters.
	 */
	public static double computeDistanceBetween(LatLng from, LatLng to) {
		return computeAngleBetween(from, to) * EARTH_RADIUS;
	}

	/**
	 * Returns the length of the given path, in meters, on Earth.
	 */
	public static double computeLength(List<LatLng> path) {
		if (path.size() < 2) {
			return 0;
		}
		double length = 0;
		LatLng prev = path.get(0);
		double prevLat = Math.toRadians(prev.latitude);
		double prevLng = Math.toRadians(prev.longitude);
		for (LatLng point : path) {
			double lat = Math.toRadians(point.latitude);
			double lng = Math.toRadians(point.longitude);
			length += distanceRadians(prevLat, prevLng, lat, lng);
			prevLat = lat;
			prevLng = lng;
		}
		return length * EARTH_RADIUS;
	}

	/**
	 * Returns the area of a closed path on Earth.
	 *
	 * @param path A closed path.
	 * @return The path's area in square meters.
	 */
	public static double computeArea(List<LatLng> path) {
		return Math.abs(computeSignedArea(path));
	}

	/**
	 * Returns the signed area of a closed path on Earth. The sign of the area may be used to
	 * determine the orientation of the path.
	 * "inside" is the surface that does not contain the South Pole.
	 *
	 * @param path A closed path.
	 * @return The loop's area in square meters.
	 */
	public static double computeSignedArea(List<LatLng> path) {
		return computeSignedArea(path, EARTH_RADIUS);
	}

	/**
	 * Returns the signed area of a closed path on a sphere of given radius.
	 * The computed area uses the same units as the radius squared.
	 * Used by SphericalUtilTest.
	 */
	static double computeSignedArea(List<LatLng> path, double radius) {
		int size = path.size();
		if (size < 3) {
			return 0;
		}
		double total = 0;
		LatLng prev = path.get(size - 1);
		double prevTanLat = Math.tan((Math.PI / 2 - Math.toRadians(prev.latitude)) / 2);
		double prevLng = Math.toRadians(prev.longitude);
		// For each edge, accumulate the signed area of the triangle formed by the North Pole
		// and that edge ("polar triangle").
		for (LatLng point : path) {
			double tanLat = Math.tan((Math.PI / 2 - Math.toRadians(point.latitude)) / 2);
			double lng = Math.toRadians(point.longitude);
			total += polarTriangleArea(tanLat, lng, prevTanLat, prevLng);
			prevTanLat = tanLat;
			prevLng = lng;
		}
		return total * (radius * radius);
	}

	/**
	 * Returns the signed area of a triangle which has North Pole as a vertex.
	 * Formula derived from "Area of a spherical triangle given two edges and the included angle"
	 * as per "Spherical Trigonometry" by Todhunter, page 71, section 103, point 2.
	 * See http://books.google.com/books?id=3uBHAAAAIAAJ&pg=PA71
	 * The arguments named "tan" are tan((pi/2 - latitude)/2).
	 */
	private static double polarTriangleArea(double tan1, double lng1, double tan2, double lng2) {
		double deltaLng = lng1 - lng2;
		double t = tan1 * tan2;
		return 2 * Math.atan2(t * Math.sin(deltaLng), 1 + t * Math.cos(deltaLng));
	}

	/**
	 * Wraps the given value into the inclusive-exclusive interval between min and max.
	 *
	 * @param n   The value to wrap.
	 * @param min The minimum.
	 * @param max The maximum.
	 */
	static double wrap(double n, double min, double max) {
		return (n >= min && n < max) ? n : (mod(n - min, max - min) + min);
	}

	/**
	 * Returns the non-negative remainder of x / m.
	 *
	 * @param x The operand.
	 * @param m The modulus.
	 */
	static double mod(double x, double m) {
		return ((x % m) + m) % m;
	}
}