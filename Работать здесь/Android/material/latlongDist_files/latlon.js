/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
/*  Latitude/longitude spherical geodesy formulae & scripts (c) Chris Veness 2002-2014            */
/*   - www.movable-type.co.uk/scripts/latlong.html                                                */
/*                                                                                                */
/*  Sample usage:                                                                                 */
/*    var p1 = new LatLon(51.5136, -0.0983);                                                      */
/*    var p2 = new LatLon(51.4778, -0.0015);                                                      */
/*    var dist = p1.distanceTo(p2);          // in km                                             */
/*    var brng = p1.bearingTo(p2);           // in degrees clockwise from north                   */
/*    ... etc                                                                                     */
/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
/*  Note that minimal error checking is performed in this example code!                           */
/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */


/**
 * Object LatLon: tools for geodetic calculations
 *
 * @requires Geo
 */
 
 
/**
 * Creates a point on the earth's surface at the supplied latitude / longitude
 *
 * @constructor
 * @param {Number} lat: latitude in degrees
 * @param {Number} lon: longitude in degrees
 * @param {Number} [radius=6371]: radius of earth if different value is required from standard 6,371km
 */
function LatLon(lat, lon, radius) {
    if (typeof(radius) == 'undefined') radius = 6371;  // earth's mean radius in km

    this.lat    = Number(lat);
    this.lon    = Number(lon);
    this.radius = Number(radius);
}


/**
 * Returns the distance from this point to the supplied point (using haversine formula)
 *
 * @this    {LatLon} latitude/longitude of origin point
 * @param   {LatLon} point: latitude/longitude of destination point
 * @param   {Number} [precision=4]: number of significant digits to use for returned value
 * @returns {Number} distance between this point and destination point, in km
 */
LatLon.prototype.distanceTo = function(point, precision) {
    // default 4 significant figures reflects typical 0.3% accuracy of spherical model
    if (typeof precision == 'undefined') precision = 4;
  
    var R = this.radius;
    var ??1 = this.lat.toRadians(),  ??1 = this.lon.toRadians();
    var ??2 = point.lat.toRadians(), ??2 = point.lon.toRadians();
    var ???? = ??2 - ??1;
    var ???? = ??2 - ??1;

    var a = Math.sin(????/2) * Math.sin(????/2) +
            Math.cos(??1) * Math.cos(??2) *
            Math.sin(????/2) * Math.sin(????/2);
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    var d = R * c;

    return d.toPrecisionFixed(Number(precision));
}


/**
 * Returns the (initial) bearing from this point to the supplied point, in degrees
 *   see http://williams.best.vwh.net/avform.htm#Crs
 *
 * @this    {LatLon} latitude/longitude of origin point
 * @param   {LatLon} point: latitude/longitude of destination point
 * @returns {Number} initial bearing in degrees from North
 */
LatLon.prototype.bearingTo = function(point) {
    var ??1 = this.lat.toRadians(), ??2 = point.lat.toRadians();
    var ???? = (point.lon-this.lon).toRadians();

    var y = Math.sin(????) * Math.cos(??2);
    var x = Math.cos(??1)*Math.sin(??2) -
            Math.sin(??1)*Math.cos(??2)*Math.cos(????);
    var ?? = Math.atan2(y, x);
  
    return (??.toDegrees()+360) % 360;
}


/**
 * Returns final bearing arriving at supplied destination point from this point; the final bearing 
 * will differ from the initial bearing by varying degrees according to distance and latitude
 *
 * @this    {LatLon} latitude/longitude of origin point
 * @param   {LatLon} point: latitude/longitude of destination point
 * @returns {Number} final bearing in degrees from North
 */
LatLon.prototype.finalBearingTo = function(point) {
    // get initial bearing from supplied point back to this point...
    var ??1 = point.lat.toRadians(), ??2 = this.lat.toRadians();
    var ???? = (this.lon-point.lon).toRadians();

    var y = Math.sin(????) * Math.cos(??2);
    var x = Math.cos(??1)*Math.sin(??2) -
            Math.sin(??1)*Math.cos(??2)*Math.cos(????);
    var ?? = Math.atan2(y, x);

    // ... & reverse it by adding 180??
    return (??.toDegrees()+180) % 360;
}


/**
 * Returns the midpoint between this point and the supplied point.
 *   see http://mathforum.org/library/drmath/view/51822.html for derivation
 *
 * @this    {LatLon} latitude/longitude of origin point
 * @param   {LatLon} point: latitude/longitude of destination point
 * @returns {LatLon} midpoint between this point and the supplied point
 */
LatLon.prototype.midpointTo = function(point) {
    var ??1 = this.lat.toRadians(), ??1 = this.lon.toRadians();
    var ??2 = point.lat.toRadians();
    var ???? = (point.lon-this.lon).toRadians();

    var Bx = Math.cos(??2) * Math.cos(????);
    var By = Math.cos(??2) * Math.sin(????);

    var ??3 = Math.atan2(Math.sin(??1)+Math.sin(??2),
                    Math.sqrt( (Math.cos(??1)+Bx)*(Math.cos(??1)+Bx) + By*By) );
    var ??3 = ??1 + Math.atan2(By, Math.cos(??1) + Bx);
    ??3 = (??3+3*Math.PI) % (2*Math.PI) - Math.PI; // normalise to -180..+180??

    return new LatLon(??3.toDegrees(), ??3.toDegrees());
}


/**
 * Returns the destination point from this point having travelled the given distance (in km) on the 
 * given initial bearing (bearing may vary before destination is reached)
 *
 *   see http://williams.best.vwh.net/avform.htm#LL
 *
 * @this    {LatLon} latitude/longitude of origin point
 * @param   {Number} brng: initial bearing in degrees
 * @param   {Number} dist: distance in km
 * @returns {LatLon} destination point
 */
LatLon.prototype.destinationPoint = function(brng, dist) {
    var ?? = Number(brng).toRadians();
    var ?? = Number(dist) / this.radius; // angular distance in radians

    var ??1 = this.lat.toRadians();
    var ??1 = this.lon.toRadians();

    var ??2 = Math.asin( Math.sin(??1)*Math.cos(??) +
                        Math.cos(??1)*Math.sin(??)*Math.cos(??) );
    var ??2 = ??1 + Math.atan2(Math.sin(??)*Math.sin(??)*Math.cos(??1),
                             Math.cos(??)-Math.sin(??1)*Math.sin(??2));
    ??2 = (??2+3*Math.PI) % (2*Math.PI) - Math.PI; // normalise to -180..+180??

    return new LatLon(??2.toDegrees(), ??2.toDegrees());
}


/**
 * Returns the point of intersection of two paths defined by point and bearing
 *
 *   see http://williams.best.vwh.net/avform.htm#Intersection
 *
 * @param   {LatLon} p1: first point
 * @param   {Number} brng1: initial bearing from first point
 * @param   {LatLon} p2: second point
 * @param   {Number} brng2: initial bearing from second point
 * @returns {LatLon} destination point (null if no unique intersection defined)
 */
LatLon.intersection = function(p1, brng1, p2, brng2) {
    var ??1 = p1.lat.toRadians(), ??1 = p1.lon.toRadians();
    var ??2 = p2.lat.toRadians(), ??2 = p2.lon.toRadians();
    var ??13 = Number(brng1).toRadians(), ??23 = Number(brng2).toRadians();
    var ???? = ??2-??1, ???? = ??2-??1;

    var ??12 = 2*Math.asin( Math.sqrt( Math.sin(????/2)*Math.sin(????/2) +
        Math.cos(??1)*Math.cos(??2)*Math.sin(????/2)*Math.sin(????/2) ) );
    if (??12 == 0) return null;

    // initial/final bearings between points
    var ??1 = Math.acos( ( Math.sin(??2) - Math.sin(??1)*Math.cos(??12) ) /
           ( Math.sin(??12)*Math.cos(??1) ) );
    if (isNaN(??1)) ??1 = 0; // protect against rounding
    var ??2 = Math.acos( ( Math.sin(??1) - Math.sin(??2)*Math.cos(??12) ) /
           ( Math.sin(??12)*Math.cos(??2) ) );

    if (Math.sin(??2-??1) > 0) {
        ??12 = ??1;
        ??21 = 2*Math.PI - ??2;
    } else {
        ??12 = 2*Math.PI - ??1;
        ??21 = ??2;
    }

    var ??1 = (??13 - ??12 + Math.PI) % (2*Math.PI) - Math.PI; // angle 2-1-3
    var ??2 = (??21 - ??23 + Math.PI) % (2*Math.PI) - Math.PI; // angle 1-2-3

    if (Math.sin(??1)==0 && Math.sin(??2)==0) return null; // infinite intersections
    if (Math.sin(??1)*Math.sin(??2) < 0) return null;      // ambiguous intersection

    //??1 = Math.abs(??1);
    //??2 = Math.abs(??2);
    // ... Ed Williams takes abs of ??1/??2, but seems to break calculation?

    var ??3 = Math.acos( -Math.cos(??1)*Math.cos(??2) +
                         Math.sin(??1)*Math.sin(??2)*Math.cos(??12) );
    var ??13 = Math.atan2( Math.sin(??12)*Math.sin(??1)*Math.sin(??2),
                          Math.cos(??2)+Math.cos(??1)*Math.cos(??3) )
    var ??3 = Math.asin( Math.sin(??1)*Math.cos(??13) +
                        Math.cos(??1)*Math.sin(??13)*Math.cos(??13) );
    var ????13 = Math.atan2( Math.sin(??13)*Math.sin(??13)*Math.cos(??1),
                           Math.cos(??13)-Math.sin(??1)*Math.sin(??3) );
    var ??3 = ??1 + ????13;
    ??3 = (??3+3*Math.PI) % (2*Math.PI) - Math.PI; // normalise to -180..+180??

    return new LatLon(??3.toDegrees(), ??3.toDegrees());
}


/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

/**
 * Returns the distance from this point to the supplied point, in km, travelling along a rhumb line
 *
 *   see http://williams.best.vwh.net/avform.htm#Rhumb
 *
 * @this    {LatLon} latitude/longitude of origin point
 * @param   {LatLon} point: latitude/longitude of destination point
 * @returns {Number} distance in km between this point and destination point
 */
LatLon.prototype.rhumbDistanceTo = function(point) {
    var R = this.radius;
    var ??1 = this.lat.toRadians(), ??2 = point.lat.toRadians();
    var ???? = ??2 - ??1;
    var ???? = Math.abs(point.lon-this.lon).toRadians();
    // if dLon over 180?? take shorter rhumb line across the anti-meridian:
    if (Math.abs(????) > Math.PI) ???? = ????>0 ? -(2*Math.PI-????) : (2*Math.PI+????);

    // on Mercator projection, longitude gets increasing stretched by latitude; q is the 'stretch factor'

    var ???? = Math.log(Math.tan(??2/2+Math.PI/4)/Math.tan(??1/2+Math.PI/4));

    // the stretch factor becomes ill-conditioned along E-W line (0/0); use empirical tolerance to avoid it
    var q = Math.abs(????) > 10e-12 ? ????/???? : Math.cos(??1);

    // distance is pythagoras on 'stretched' Mercator projection
    var ?? = Math.sqrt(????*???? + q*q*????*????); // angular distance in radians
    var dist = ?? * R;

    return dist.toPrecisionFixed(4); // 4 sig figs reflects typical 0.3% accuracy of spherical model
}


/**
 * Returns the bearing from this point to the supplied point along a rhumb line, in degrees
 *
 * @this    {LatLon} latitude/longitude of origin point
 * @param   {LatLon} point: latitude/longitude of destination point
 * @returns {Number} bearing in degrees from North
 */
LatLon.prototype.rhumbBearingTo = function(point) {
    var ??1 = this.lat.toRadians(), ??2 = point.lat.toRadians();
    var ???? = (point.lon-this.lon).toRadians();
    // if dLon over 180?? take shorter rhumb line across the anti-meridian:
    if (Math.abs(????) > Math.PI) ???? = ????>0 ? -(2*Math.PI-????) : (2*Math.PI+????);

    var ???? = Math.log(Math.tan(??2/2+Math.PI/4)/Math.tan(??1/2+Math.PI/4));

    var ?? = Math.atan2(????, ????);

    return (??.toDegrees()+360) % 360;
}


/**
 * Returns the destination point from this point having travelled the given distance (in km) on the 
 * given bearing along a rhumb line
 *
 * @this    {LatLon} latitude/longitude of origin point
 * @param   {Number} brng: bearing in degrees from North
 * @param   {Number} dist: distance in km
 * @returns {LatLon} destination point
 */
LatLon.prototype.rhumbDestinationPoint = function(brng, dist) {
    var ?? = Number(dist) / this.radius; // angular distance in radians
    var ??1 = this.lat.toRadians(), ??1 = this.lon.toRadians();
    var ?? = Number(brng).toRadians();

    var ???? = ?? * Math.cos(??);

    var ??2 = ??1 + ????;
    // check for some daft bugger going past the pole, normalise latitude if so
    if (Math.abs(??2) > Math.PI/2) ??2 = ??2>0 ? Math.PI-??2 : -Math.PI-??2;

    var ???? = Math.log(Math.tan(??2/2+Math.PI/4)/Math.tan(??1/2+Math.PI/4));
    var q = Math.abs(????) > 10e-12 ? ???? / ???? : Math.cos(??1); // E-W course becomes ill-conditioned with 0/0

    var ???? = ??*Math.sin(??)/q;

    var ??2 = ??1 + ????;

    ??2 = (??2 + 3*Math.PI) % (2*Math.PI) - Math.PI; // normalise to -180..+180??

    return new LatLon(??2.toDegrees(), ??2.toDegrees());
}


/**
 * Returns the loxodromic midpoint (along a rhumb line) between this point and the supplied point.
 *   see http://mathforum.org/kb/message.jspa?messageID=148837
 *
 * @this    {LatLon} latitude/longitude of origin point
 * @param   {LatLon} point: latitude/longitude of destination point
 * @returns {LatLon} midpoint between this point and the supplied point
 */
LatLon.prototype.rhumbMidpointTo = function(point) {
    var ??1 = this.lat.toRadians(), ??1 = this.lon.toRadians();
    var ??2 = point.lat.toRadians(), ??2 = point.lon.toRadians();

    if (Math.abs(??2-??1) > Math.PI) ??1 += 2*Math.PI; // crossing anti-meridian

    var ??3 = (??1+??2)/2;
    var f1 = Math.tan(Math.PI/4 + ??1/2);
    var f2 = Math.tan(Math.PI/4 + ??2/2);
    var f3 = Math.tan(Math.PI/4 + ??3/2);
    var ??3 = ( (??2-??1)*Math.log(f3) + ??1*Math.log(f2) - ??2*Math.log(f1) ) / Math.log(f2/f1);

    if (!isFinite(??3)) ??3 = (??1+??2)/2; // parallel of latitude

    ??3 = (??3 + 3*Math.PI) % (2*Math.PI) - Math.PI; // normalise to -180..+180??

    return new LatLon(??3.toDegrees(), ??3.toDegrees());
}


/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */


/**
 * Returns a string representation of this point; format and dp as per lat()/lon()
 *
 * @this    {LatLon} latitude/longitude of origin point
 * @param   {String} [format]: return value as 'd', 'dm', 'dms'
 * @param   {Number} [dp=0|2|4]: number of decimal places to display
 * @returns {String} comma-separated latitude/longitude
 */
LatLon.prototype.toString = function(format, dp) {
    if (typeof format == 'undefined') format = 'dms';

    return Geo.toLat(this.lat, format, dp) + ', ' + Geo.toLon(this.lon, format, dp);
}


/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */


// ---- extend Number object with methods for converting degrees/radians


/** Converts numeric degrees to radians */
if (typeof Number.prototype.toRadians == 'undefined') {
    Number.prototype.toRadians = function() {
        return this * Math.PI / 180;
    }
}


/** Converts radians to numeric (signed) degrees */
if (typeof Number.prototype.toDegrees == 'undefined') {
    Number.prototype.toDegrees = function() {
        return this * 180 / Math.PI;
    }
}


/** 
 * Formats the significant digits of a number, using only fixed-point notation (no exponential)
 * 
 * @param   {Number} precision: Number of significant digits to appear in the returned string
 * @returns {String} A string representation of number which contains precision significant digits
 */
if (typeof Number.prototype.toPrecisionFixed == 'undefined') {
    Number.prototype.toPrecisionFixed = function(precision) {

    // use standard toPrecision method
    var n = this.toPrecision(precision);

    // ... but replace +ve exponential format with trailing zeros
    n = n.replace(/(.+)e\+(.+)/, function(n, sig, exp) {
        sig = sig.replace(/\./, '');       // remove decimal from significand
        l = sig.length - 1;
        while (exp-- > l) sig = sig + '0'; // append zeros from exponent
        return sig;
    });

    // ... and replace -ve exponential format with leading zeros
    n = n.replace(/(.+)e-(.+)/, function(n, sig, exp) {
        sig = sig.replace(/\./, '');       // remove decimal from significand
        while (exp-- > 1) sig = '0' + sig; // prepend zeros from exponent
        return '0.' + sig;
    });

    return n;
  }
}


/** Trims whitespace from string (q.v. blog.stevenlevithan.com/archives/faster-trim-javascript) */
if (typeof String.prototype.trim == 'undefined') {
    String.prototype.trim = function() {
        return String(this).replace(/^\s\s*/, '').replace(/\s\s*$/, '');
    }
}


/** Returns the sign of a number, indicating whether the number is positive, negative or zero */
if (typeof Math.sign == 'undefined') {
    // stackoverflow.com/questions/7624920/number-sign-in-javascript
    Math.sign = function(x) {
        return typeof x === 'number' ? x ? x < 0 ? -1 : 1 : x === x ? 0 : NaN : NaN;
    }
}


/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
if (!window.console) window.console = { log: function() {} };
