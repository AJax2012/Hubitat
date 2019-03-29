/**
 *  Custom DarkSky Driver
 *
 *  Thank you to Andrew Parker @CobraVmax for supplying most of the base code
 *  Cobra's original project can be viewed at: 
 *  https://github.com/CobraVmax/Hubitat/blob/master/Drivers/Weather/WeatherUndergroundCustom.groovy
 *
 *  This driver was originally written by @mattw01 and I thank him for that!
 *  Heavily modified by myself: @Cobra with lots of help from @Scottma61 ( @Matthew ) and @AJax2012 ( @Adam)
 *  and with valuable input from the Hubitat community
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Last Update 19/03/2019
 *
 *  V3.3.0 - Add Dashboard Tile - @AJax2012 24/03/2019
 *  V3.2.0 - Change Compatibility to DarkSky - @AJax2012 19/03/2019
 *  V3.1.0 - Added Icons for current and forecast weather for use with new tile app
 *  V3.0.0 - Updated info checking.
 *  V2.9.0 - Changed with way 'alerts' are handled for US/Non US timezones
 *  V2.8.1 - Debug Poll command
 *  V2.8.0 - Added switchable 'forecastIcon' to show current or forcast icon
 *  V2.7.0 - Added 'forecastIcon' for use with Sharptools
 *  V2.6.0 - Updated remote version checking
 *  V2.5.0 - Removed capabilities/attributes switch and reformatted all in lowercase - @Cobra 04/05/2018
 *  V2.4.1 - Debug - Changed the switchable capabilities to allow them to be seen by 'rule machine'- @Cobra 03/05/2018
 *  V2.4.0 - Added switchable 'Capabilities & Lowercase Data' for use with dashboards & Rule Machine - @Cobra 02/05/2018
 *  V2.3.0 - Added Moon phase and illumination percentage - @Cobra 01/05/2018
 *  V2.2.0 - Added 'Sunrise' and 'Sunset' - Thanks to: @Scottma61 for this one - @Cobra 01/05/2018
 *  V2.1.1 - Added defaultValue to "pollIntervalLimit" to prevent errors on new installs - @Cobra 01/05/2018
 *  V2.1.0 - Added 3 attributes - Rain tomorrow & the day after and Station_State also added poll counter and reset button @Cobra 01/05/2018
 *  V2.0.1 - Changed to one call to WU for Alerts, Conditions and Forecast - Thanks to: @Scottma61 for this one
 *  V2.0.0 - version alignment with lowercase version - @Cobra 27/04/2018 
 *  V1.9.0 - Added 'Chance_Of_Rain' an an attribute (also added to the summary) - @Cobra 27/04/2018 
 *  V1.8.0 - added 'stateChange' to some of the params that were not updating on poll unless changed - @Cobra 27/04/2018 
 *  V1.7.2 - Debug on lowercase version - updated version number for consistancy - @Cobra 26/04/2018 
 *  V1.7.1 - Debug - @Cobra 26/04/2018 
 *  V1.7.0 - Added 'Weather Summary' as a summary of the data with some English in between @Cobra - 26/04/2018
 *  V1.6.0 - Changed some attribute names - @Cobra - 25/04/2018/
 *  V1.5.0 - Added 'Station ID' so you can confirm you are using correct WU station @Cobra 25/04/2018
 *  V1.4.0 - Added ability to choose 'Pressure', 'Distance/Speed' & 'Precipitation' units & switchable logging- @Cobra 25/04/2018
 *  V1.3.0 - Added wind gust - removed some capabilities and added attributes - @Cobra 24/04/2018
 *  V1.2.0 - Added wind direction - @Cobra 23/04/2018
 *  V1.1.0 - Added ability to choose between "Fahrenheit" and "Celsius" - @Cobra 23/03/2018
 *  V1.0.0 - Original @mattw01 version
 *
 */

metadata {
    definition (name: "DarkSky Weather Driver 2", namespace: "AJax", author: "Adam Gardner") {
        capability "Actuator";
        capability "Sensor";
        capability "Temperature Measurement";
        capability "Relative Humidity Measurement";
        command "poll";
        command "ForcePoll";

        attribute "solarradiation", "number";
        attribute "observationTime", "string";
        attribute "localSunrise", "string";
        attribute "localSunset", "string";
        attribute "weather", "string";
        attribute "feelsLike", "number";
        attribute "forecastIcon", "string";
		attribute "weatherIcon", "string";
        attribute "percentPrecip", "string";
        attribute "pressure", "number";
        attribute "dewpoint", "number";
        attribute "visibility", "number";
        attribute "forecastHigh", "number";
        attribute "forecastLow", "number";
        attribute "forecastConditions", "string";
        attribute "windDir", "string";
        attribute "windGust", "string";
        attribute "precip_1hr", "number";
        attribute "precipToday", "number";
        attribute "wind", "number";
        attribute "UV", "number";
        attribute "pollsSinceReset", "number";
        attribute "temperatureUnit", "string";
        attribute "distanceMeasurement", "string";
        attribute "pressureUnit", "string";
        attribute "rainUnit", "string";
        attribute "summaryFormat", "string";
        attribute "alert", "string";
        attribute "weatherSummary", "string";
        attribute "weatherSummaryFormat", "string";
        attribute "precipTomorrow", "string";
        attribute "precipDayAfterTomorrow", "string";
        attribute "moonPhase", "string";
 		attribute "DriverAuthor", "string";
        attribute "DriverVersion", "string";
        attribute "DriverStatus", "string";
		attribute "DriverUpdate", "string";
        attribute "myTile", "string";
    }

    preferences() {
        section("Query Inputs"){
            input "apiKey", "text", required: true, title: "API Key";
            input "lat", "text", required: true, title: "Latitude";
            input "lng", "text", required: true, title: "Longitude";
            input "city", "text", required: true, title: "City";
            input "homeState", "text", required: true, title: "State";
            input "tempFormat", "enum", required: true, title: "Display Unit - Temperature: Fahrenheit or Celsius",  options: ["Fahrenheit", "Celsius"];
            input "distanceFormat", "enum", required: true, title: "Display Unit - Distance/Speed: Miles or Kilometres",  options: ["Miles (mph)", "Kilometres (kph)"];
            input "pressureFormat", "enum", required: true, title: "Display Unit - Pressure: Inches or Millibar",  options: ["Inches", "Millibar"];
            input "rainFormat", "enum", required: true, title: "Display Unit - Precipitation: Inches or Millimetres",  options: ["Inches", "Millimetres"];
            input "dateFormat", "enum", required: true, title: "Display Unit - Date",  options: ["yyyy/MM/dd", "yyyy/M/d", "MM/dd/yyyy", "M/d/yyyy", "dd/MM/yyyy", "d/M/yyyy"];
            input "timeFormat", "enum", required: true, title: "Display Unit - Time",  options: ["24-hour (17:15)", "12-hour long (05:15 PM)", "12-hour short (5:15 PM)"];
            input "pollIntervalLimit", "number", title: "Poll Interval Limit:", required: true, defaultValue: 1;
            input "autoPoll", "bool", required: false, title: "Enable Auto Poll";
            input "pollInterval", "enum", title: "Auto Poll Interval:", required: false, defaultValue: "5 Minutes", options: ["5 Minutes", "10 Minutes", "15 Minutes", "30 Minutes", "1 Hour", "3 Hours"];
            input "logSet", "bool", title: "Log All WU Response Data", required: true, defaultValue: false;
            input "cutOff", "time", title: "New Day Starts", required: true;
            input "summaryType", "bool", title: "Full Weather Summary", required: true, defaultValue: false;
            input "iconType", "bool", title: "Icon: On = Current - Off = Forecast", required: true, defaultValue: false;
            input "weatherFormat", "enum", required: true, title: "How to format weather summary",  options: ["Celsius, Miles & MPH", "Fahrenheit, Miles & MPH", "Celsius, Kilometres & KPH"];
        }
    }
}

def installed() {
    initialize();
}

def updated() {
    initialize();
}

def initialize() {
    unschedule();
    if (autoPoll){
        Schedule();
    }
}

def poll() {
    ForcePoll();
}

def ForcePoll()
{
    log.debug "ForcePoll called";
    def params = [uri: "https://api.darksky.net/forecast/${apiKey}/${lat},${lng}"];
    
    try {
        httpGet(params) { response ->
        
            response.headers.each {
                log.debug "Response: ${it.name} : ${it.value}"
            }

            if(logSet == true) {  
                log.debug "params: ${params}"
                log.debug "response contentType: ${response.contentType}"
                log.debug "response data: ${response.data}"
            }

            if(logSet == false) { 
                log.info "Further WU detailed data logging disabled"    
            }

            def current = response.data.currently;
            def today = response.data.daily.data[0];
            def tomorrow = response.data.daily.data[1];
            
            // reused values
            def summary = current.summary;
            def todayHigh = (int) today.temperatureHigh;
            def todayLow = (int) today.temperatureLow;
            def currentTemp = (int) current.temperature;
            def apparentTemp = (int) current.apparentTemperature;
            def humidity = (int) (current.humidity * 100);
            def windSpeed = (int) current.windSpeed;
            def windGust = (int) current.windGust;
            def todayPrecipProbability = (int) (today.precipProbability * 100);
            def currentPreciptProbability = (int) (current.precipProbability * 100)
            def precipType = today.precipType;
            def visibility = current.visibility;
            def currentDateTime = new Date((long) current.time * 1000);

            // define wind direction
            def windBearing = response.data.currently.windBearing;
            def windDirection = "";

            if (windBearing >= 0 && windBearing < 20 || windBearing >= 340 && windBearing < 360 )
                windDirection = "North"
            if (windBearing >= 20 && windBearing < 60)
                windDirection = "North East"
            if (windBearing >= 60 && windBearing < 120)
                windDirection = "East"
            if (windBearing >= 120 && windBearing < 160)
                windDirection = "South East"
            if (windBearing >= 160 && windBearing < 200)
                windDirection = "South"
            if (windBearing >= 200 && windBearing < 260)
                windDirection = "South West"
            if (windBearing >= 260 && windBearing < 300)
                windDirection = "West"
            if (windBearing >= 300 && windBearing < 340)
                windDirection = "North West"
            
            // start setting values
            sendEvent(name: "pollsSinceReset", value: state.NumOfPolls, isStateChange: true);
            sendEvent(name: "moonPhase", value: tomorrow.moonPhase , isStateChange: true);

            // get sunset date/time
            // cal.setTimeZone(response.data.timezone);
            def sunriseTime = new Date((long) today.sunriseTime * 1000);
            def sunsetTime = new Date((long) today.sunsetTime * 1000);

            // format time
            def format = "";

            if (timeFormat == "24-hour (17:15)")
                format = "HH:mm"
                
            if (timeFormat == "12-hour long (05:15 PM)")
                format = "hh:mm a"
             
             if (timeFormat == "12-hour short (5:15 PM)")
                format = "h:mm a"

            // set formatted sunrise/sunset times
            sendEvent(name: "localSunrise", value: sunriseTime.format(format), descriptionText: "Sunrise today is at $localSunrise", isStateChange: true);

            sendEvent(name: "localSunset", value: sunsetTime.format(format), descriptionText: "Sunset today at is $localSunset", isStateChange: true);

            def rainUnit = "";
            def tempUnit = "";
            def distanceMeasurementShort = "";
            def pressureUnit = "";

            if(rainFormat == "Inches") {
                sendEvent(name: "rainUnit", value: "Inches", isStateChange: true);
                rainUnit = "in";
            }
            
            if(rainFormat == "Millimetres") {
                sendEvent(name: "rainUnit", value: "Millimetres", isStateChange: true);
                rainUnit = "mm";
            }

            if(pressureFormat == "Inches") {
                sendEvent(name: "pressureUnit", value: "Inches");
                pressureUnit = "in";
            }
            
            if(pressureFormat == "Millibar") {
                sendEvent(name: "pressureUnit", value: "Inches");
                pressureUnit = "mb";
            }

            if(tempFormat == "Fahrenheit") { 
                sendEvent(name: "temperatureUnit", value: "Fahrenheit", isStateChange: true);
                tempUnit = "F";
            }

            if(tempFormat == "Celsius") { 
                sendEvent(name: "temperatureUnit", value: "Celsius", isStateChange: true);
                tempUnit = "C";
            }

            if(distanceFormat == "Miles (mph)") {
                sendEvent(name: "distanceMeasurement", value: "Miles (mph)", isStateChange: true);
                speedMeasurement = "mph";
                distanceMeasurement = "Miles";
                distanceMeasurementShort = "mi";
            }

            if(distanceFormat == "Kilometres (kph)") {
                sendEvent(name: "distanceMeasurement", value: "Kilometres (kph)", isStateChange: true);
                speedMeasurement = "kph";
                distanceMeasurement = "Kilometres";
                distanceMeasurementShort = "km";
            }

            // set temps/precip/forecast/pressure/wind/etc.
            def precipToday = (int) (today.precipIntensity * 100);
            def precipTomorrow = (int) (tomorrow.precipIntensity * 100)
            def preciptDayAfterTomorrow = (int) (response.data.daily.data[2].precipIntensity * 100)

            sendEvent(name: "percentPrecip", value: currentPreciptProbability, isStateChange: true);
            sendEvent(name: "precipToday", value: precipToday, unit: rainUnit, isStateChange: true);
            sendEvent(name: "precipTomorrow", value: precipTomorrow, unit: rainUnit, isStateChange: true);
            sendEvent(name: "precipDayAfterTomorrow", value: preciptDayAfterTomorrow, unit: rainUnit, isStateChange: true);
            sendEvent(name: "temperature", value: currentTemp, unit: tempUnit, isStateChange: true);
            sendEvent(name: "feelsLike", value: apparentTemp, unit: tempUnit, isStateChange: true);
            sendEvent(name: "weather", value: summary, isStateChange: true);
            sendEvent(name: "humidity", value: humidity, isStateChange: true);
            sendEvent(name: "dewpoint", value: (int) current.dewPoint, unit: tempUnit, isStateChange: true);
            sendEvent(name: "forecastHigh", value: (int) tomorrow.temperatureHigh, unit: tempUnit, isStateChange: true);
            sendEvent(name: "forecastLow", value: (int) tomorrow.temperatureLow, unit: tempUnit, isStateChange: true);
            sendEvent(name: "visibility", value: "${visibility} ${distanceMeasurementShort}", unit: distanceMeasurementShort, isStateChange: true);
            sendEvent(name: "wind", value: "${windSpeed} ${speedMeasurement}", unit: speedMeasurement, isStateChange: true);
            sendEvent(name: "windGust", value: "${windGust} ${speedMeasurement}", isStateChange: true);
            sendEvent(name: "windDir", value: windDirection, isStateChange: true);
            sendEvent(name: "pressure", value: current.pressure, unit: pressureUnit, isStateChange: true);
            sendEvent(name: "UV", value: current.uvIndex, isStateChange: true);
            sendEvent(name: "forecastConditions", value: tomorrow.summary, isStateChange: true);
            sendEvent(name: "observationTime", value: currentDateTime.format(dateFormat), isStateChange: true);

            // Select Icon 
            sendEvent(name: "weatherIcon", value: summary.replaceAll("\\s", "").toLowerCase(), isStateChange: true);
            sendEvent(name: "forecastIcon", value: tomorrow.icon, isStateChange: true);
            
            def distanceMeasurement = "";
            def speedMeasurement = "";

            def builder = new StringBuilder();
            
            // start summary string builder section
            if(summaryType == true) {

                if (weatherFormat == "Celsius, Miles & MPH"){
                    sendEvent(name: "weatherFormat", value: "Celsius, Miles & MPH", isStateChange: true)
                    distanceMeasurement = "miles";
                    speedMeasurement = "mph";
                }
                    
                if (weatherFormat == "Fahrenheit, Miles & MPH"){
                    sendEvent(name: "weatherFormat", value: "Fahrenheit, Miles & MPH", isStateChange: true)
                    distanceMeasurement = "miles";
                    speedMeasurement = "mph";
                }
                    
                if (weatherFormat == "Celsius, Kilometres & KPH"){
                    sendEvent(name: "weatherFormat", value: "Celsius, Kilometres & KPH", isStateChange: true)
                    distanceMeasurement = "kilometres";
                    speedMeasurement = "kph";
                }

                builder = new StringBuilder();
                builder.append("Weather summary: ");
                builder.append(currentDateTime.format(dateFormat));
                builder.append(". ");
                builder.append(summary);
                builder.append(" with a high of ");
                builder.append(todayHigh);
                builder.append(" degrees, and a low of ");
                builder.append(todayLow);
                builder.append(" degrees. Humidity is currently around ");
                builder.append(humidity);
                builder.append("% and temperature is ");
                builder.append(currentTemp);
                builder.append(" degrees.  The temperature feels like it's ");
                builder.append(apparentTemp);
                builder.append(" degrees. Wind is from the ");
                builder.append(windDirection);
                builder.append(" at ");
                builder.append("${windSpeed} ${speedMeasurement}");
                builder.append(", with gusts up to ");
                builder.append("${windGust}${speedMeasurement}");
                builder.append(". Visibility is around ");
                builder.append("${visibility} ${distanceMeasurement}");
                builder.append(". There is a ");
                builder.append(todayPrecipProbability);
                builder.append("% chance of ");
                builder.append(precipType);
                builder.append(" today.");

                sendEvent(name: "weatherSummary", value: builder.toString(), isStateChange: true);
            }
            
            // start short summary string builder section
            if (summaryType == false) {
                
                if (weatherFormat == "Celsius, Miles & MPH") {
                    sendEvent(name: "weatherFormat", value: "Celsius, Miles & MPH", isStateChange: true)
                    distanceMeasurement = "miles";
                    speedMeasurement = "mph";
                }
            
                if (weatherFormat == "Fahrenheit, Miles & MPH") {
                    sendEvent(name: "weatherFormat", value: "Fahrenheit, Miles & MPH", isStateChange: true)
                    distanceMeasurement = "miles";
                    speedMeasurement = "mph";
                }
            
                if (weatherFormat ==  "Celsius, Kilometres & KPH") {
                    sendEvent(name: "weatherFormat", value:  "Celsius, Kilometres & KPH", isStateChange: true)
                    distanceMeasurement = "kilometres";
                    speedMeasurement = "KPH";
                }

                builder = new StringBuilder();
                builder.append(summary);
                builder.append(". Forecast High:");
                builder.append(todayHigh);
                builder.append(", Forecast Low: ");
                builder.append(todayLow);
                builder.append(". Humidity: ");
                builder.append(humidity);
                builder.append("%. Temperature: ");
                builder.append(currentTemp);
                builder.append(". Wind Direction: ");
                builder.append(windDirection);
                builder.append(".  Wind Speed: ");
                builder.append("${windSpeed}${speedMeasurement}");
                builder.append(". Gust: ");
                builder.append("${windGust}${speedMeasurement}");
                builder.append(". ");
                builder.append(precipType);
                builder.append(": ");
                builder.append(todayPrecipProbability);
                builder.append("%");

                sendEvent(name: "weatherSummary", value: builder.toString(), isStateChange: true);
            }

            def mytext = new StringBuilder();
            if (currentTemp < 45 ){
                mytext.append("<style>.temp{color:#1d62f0;font-size:1.25em;font-weight:500;}</style>")
            } else if (currentTemp > 75) {
                mytext.append("<style>.temp{color:#8b0000;font-size:1.25em;font-weight:500;}</style>")
            }
            mytext.append("<style>#low{color:#1d62f0;font-size:1.25em;font-weight:500;}</style>")
            mytext.append("<style>#high{color:#8b0000;font-size:1.25em;font-weight:500;}</style>")
            mytext.append("<div style='text-align:center;display:block;margin-top:0em;margin-bottom:0em;padding-top:5px;font-size:0.9em;line-height:100%;'>${city}, ${homeState}</div>");
            mytext.append("<div style='display:block;margin:0;font-size:0.75em;line-height:100%;'>");
            mytext.append("<img style='width:20%;height:auto;display:block;margin-right:auto;margin-left:auto;' src='${getImgName(summary)}'></div>");
            mytext.append("<div style='text-align:center;display:block;margin-top:0;margin-bottom:0;padding-bottom:5px;font-size:0.7em;line-height:100%;'>Currently: ");
            mytext.append("<span class='temp'>${currentTemp}&deg${tempUnit}</span>. Feels like <span class='temp'>${apparentTemp}<span class='temp'>&deg${tempUnit}</div>");
            mytext.append("<div style='text-align:center;display:block;margin-top:0;margin-bottom:0;font-size:0.7em;line-height:100%;'>High: <span id='high'>${todayHigh}&deg${tempUnit}</span>, Low: <span id='low'>${todayLow}&deg${tempUnit}</span></div>");
            mytext.append("<div style='width:100%;line-height:100%;display:block;font-size:0.7em;margin:0;padding:0;'>");
            mytext.append("<span style='float:left;padding-top:7px'>${summary}</span>");
            mytext.append("<span style='float:right;'>${windSpeed}${speedMeasurement}");
            mytext.append("<img style='width:20px;height:auto;margin-left:5px;margin-right:5px;' src='https://cdn3.iconfinder.com/data/icons/weather-ios-11-black-white/50/Breezy_Wind_Cold_Apple_iOS_Flat_Weather-512.png'>");
            mytext.append("</span></div>");
            mytext.append("<div style='width:100%;line-height:100%;display:block;margin:0;padding-top:25px;font-size:0.6em;'>");
            mytext.append("<span style='float:left;'>${sunriseTime.format(format)}");
            mytext.append("<img style='width:20px;height:auto;margin-left:5px;margin-right:5px;' src='http://topafricatrek.com/wp-content/uploads/2015/04/sun-symbol-512.png'>");
            mytext.append("${sunsetTime.format(format)}</span>");
            mytext.append("<span style='float:right;'>${humidity}% ");
            mytext.append("<img style='width:15px;height:auto;margin-left:5px;margin-right:5px;' src='https://cdn1.iconfinder.com/data/icons/weather-line-icon-set-3/100/humidity-512.png'>");
            mytext.append("${todayPrecipProbability}% ");
            if (precipType != "snow"){
                mytext.append("<img style='width:15px;height:auto;margin-left:5px;margin-right:5px;' src='https://image.flaticon.com/icons/png/512/45/45768.png'>");
            } else {
                mytext.append("<img style='width:15px;height:auto;margin-left:5px;margin-right:5px;' src='https://static.thenounproject.com/png/64-200.png'>");
            }
            mytext.append("</span></div>");
            sendEvent(name: "myTile", value: mytext.toString(), displayed: true)
                    
            
        }
    } catch (e) {
        log.error "something went wrong: $e";
    }
}

def Schedule() {
    def array = pollInterval.split();
    def numb = array[0];
    def unit = array[1];

    if (unit.contains("Second")) {
        schedule("0/${numb} * * ? * * *", poll);
    } else {
        schedule("${numb} * * ? * * *", poll);
    }
}

def Report(){
    def obvTime = observationTime.value
    log.info "$obvTime"  
}

def setVersion(){
    state.version = "3.2.0"
    state.InternalName = "DarkSky Weather Driver"
    sendEvent(name: "DriverAuthor", value: "Adam Gardner", isStateChange: true)
    sendEvent(name: "DriverVersion", value: state.version, isStateChange: true)
}

private getImgName(wuphrase){
    url = state.iconStore
    LUitem = LUTable.find{ (state.extSource==1 ? it.wuphrase : (state.extSource == 2 ? it.xucode : it.wuphrase)) == wuphrase && it.day == state.is_day }    
    return (url + (LUitem ? LUitem.img : 'na.png') + (((state.iconStore.toLowerCase().contains('://github.com/')) && (state.iconStore.toLowerCase().contains('/blob/master/'))) ? "?raw=true" : ""))    
}



def LUTable =     [
	 [xucode: 1000, wuphrase: 'Clear', wucode: 'sunny', day: 1, img: '32.png', luxpercent: 1],   // DAY: Sunny - Clear
     [xucode: 1003, wuphrase: 'Partly Cloudy', wucode: 'partlycloudy', day: 1, img: '30.png', luxpercent: 0.8],   // DAY: Partly cloudy
     [xucode: 1003, wuphrase: 'Scattered Clouds', wucode: 'partlycloudy', day: 1, img: '30.png', luxpercent: 0.8],   // DAY: Partly cloudy - Scattered Clouds
     [xucode: 1006, wuphrase: 'Mostly Cloudy', wucode: 'cloudy', day: 1, img: '26.png', luxpercent: 0.6],   // DAY: Cloudy - Mostly Cloudy
     [xucode: 1009, wuphrase: 'Overcast', wucode: 'cloudy', day: 1, img: '28.png', luxpercent: 0.6],   // DAY: Overcast
     [xucode: 1030, wuphrase: 'Hazy', wucode: 'hazy', day: 1, img: '20.png', luxpercent: 0.2],   // DAY: Mist
     [xucode: 1063, wuphrase: 'Rain', wucode: 'rain', day: 1, img: '39.png', luxpercent: 0.5],   // DAY: Patchy rain possible - Rain
     [xucode: 1066, wuphrase: 'Light Thunderstorms and Snow', wucode: 'chancesnow', day: 1, img: '41.png', luxpercent: 0.3],   // DAY: Patchy snow possible - Light Thunderstorms and Snow
     [xucode: 1069, wuphrase: 'Ice Pellets', wucode: 'sleet', day: 1, img: '17.png', luxpercent: 0.5],   // DAY: Patchy sleet possible - Ice Pellets
     [xucode: 1072, wuphrase: 'Light Freezing Drizzle', wucode: 'sleet', day: 1, img: '6.png', luxpercent: 0.3],   // DAY: Patchy freezing drizzle possible - Light Freezing Drizzle
     [xucode: 1087, wuphrase: 'Thunderstorm', wucode: 'tstorms', day: 1, img: '3.png', luxpercent: 0.3],   // DAY: Thundery outbreaks possible - Thunderstorm
     [xucode: 1216, wuphrase: 'Snow', wucode: 'snow', day: 1, img: '7.png', luxpercent: 0.3],   // DAY: Patchy moderate snow - Snow
	 [xucode: 1114, wuphrase: 'Blowing Snow', wucode: 'snow', day: 1, img: '15.png', luxpercent: 0.3],   // DAY: Blowing snow
     [xucode: 1114, wuphrase: 'Heavy Blowing Snow', wucode: 'snow', day: 1, img: '15.png', luxpercent: 0.3],   // DAY: Blowing snow - Heavy Blowing Snow
     [xucode: 1114, wuphrase: 'Heavy Low Drifting Snow', wucode: 'snow', day: 1, img: '15.png', luxpercent: 0.3],   // DAY: Blowing snow - Heavy Low Drifting Snow
     [xucode: 1114, wuphrase: 'Heavy Snow Blowing Snow Mist', wucode: 'snow', day: 1, img: '15.png', luxpercent: 0.3],   // DAY: Blowing snow - Heavy Snow Blowing Snow Mist
     [xucode: 1114, wuphrase: 'Light Blowing Snow', wucode: 'snow', day: 1, img: '15.png', luxpercent: 0.3],   // DAY: Blowing snow - Light Blowing Snow
     [xucode: 1114, wuphrase: 'Light Low Drifting Snow', wucode: 'snow', day: 1, img: '15.png', luxpercent: 0.3],   // DAY: Blowing snow - Light Low Drifting Snow
     [xucode: 1114, wuphrase: 'Light Snow Blowing Snow Mist', wucode: 'snow', day: 1, img: '15.png', luxpercent: 0.3],   // DAY: Blowing snow - Light Snow Blowing Snow Mist
     [xucode: 1114, wuphrase: 'Low Drifting Snow', wucode: 'snow', day: 1, img: '15.png', luxpercent: 0.3],   // DAY: Blowing snow - Low Drifting Snow
     [xucode: 1117, wuphrase: 'Heavy Snow', wucode: 'snow', day: 1, img: '41.png', luxpercent: 0.3],   // DAY: Blizzard - Heavy Snow
     [xucode: 1135, wuphrase: 'Fog', wucode: 'fog', day: 1, img: '20.png', luxpercent: 0.2],   // DAY: Fog
     [xucode: 1135, wuphrase: 'Fog Patches', wucode: 'fog', day: 1, img: '20.png', luxpercent: 0.2],   // DAY: Fog - Fog Patches
     [xucode: 1135, wuphrase: 'Hazy', wucode: 'fog', day: 1, img: '20.png', luxpercent: 0.2],   // DAY: Fog - Haze
     [xucode: 1135, wuphrase: 'Heavy Fog', wucode: 'fog', day: 1, img: '20.png', luxpercent: 0.2],   // DAY: Fog - Heavy Fog
     [xucode: 1135, wuphrase: 'Heavy Fog Patches', wucode: 'fog', day: 1, img: '20.png', luxpercent: 0.2],   // DAY: Fog - Heavy Fog Patches
     [xucode: 1135, wuphrase: 'Light Fog', wucode: 'fog', day: 1, img: '20.png', luxpercent: 0.2],   // DAY: Fog - Light Fog
     [xucode: 1135, wuphrase: 'Light Fog Patches', wucode: 'fog', day: 1, img: '20.png', luxpercent: 0.2],   // DAY: Fog - Light Fog Patches
     [xucode: 1135, wuphrase: 'Mist', wucode: 'fog', day: 1, img: '20.png', luxpercent: 0.2],   // DAY: Fog - Mist
     [xucode: 1135, wuphrase: 'Partial Fog', wucode: 'fog', day: 1, img: '20.png', luxpercent: 0.2],   // DAY: Fog - Partial Fog
     [xucode: 1135, wuphrase: 'Shallow Fog', wucode: 'fog', day: 1, img: '20.png', luxpercent: 0.2],   // DAY: Fog - Shallow Fog
     [xucode: 1147, wuphrase: 'Freezing Fog', wucode: 'fog', day: 1, img: '21.png', luxpercent: 0.2],   // DAY: Freezing fog
     [xucode: 1147, wuphrase: 'Heavy Freezing Fog', wucode: 'fog', day: 1, img: '21.png', luxpercent: 0.2],   // DAY: Freezing fog - Heavy Freezing Fog
     [xucode: 1147, wuphrase: 'Light Freezing Fog', wucode: 'fog', day: 1, img: '21.png', luxpercent: 0.2],   // DAY: Freezing fog - Light Freezing Fog
     [xucode: 1147, wuphrase: 'Patches of Fog', wucode: 'fog', day: 1, img: '21.png', luxpercent: 0.2],   // DAY: Freezing fog - Patches of Fog
     [xucode: 1150, wuphrase: 'Light Drizzle', wucode: 'rain', day: 1, img: '9.png', luxpercent: 0.5],   // DAY: Patchy light drizzle - Light Drizzle
     [xucode: 1153, wuphrase: 'Drizzle', wucode: 'rain', day: 1, img: '9.png', luxpercent: 0.5],   // DAY: Light drizzle - Drizzle
     [xucode: 1153, wuphrase: 'Light Drizzle', wucode: 'rain', day: 1, img: '9.png', luxpercent: 0.5],   // DAY: Light drizzle
     [xucode: 1153, wuphrase: 'Light Mist', wucode: 'rain', day: 1, img: '9.png', luxpercent: 0.5],   // DAY: Light drizzle - Light Mist
     [xucode: 1153, wuphrase: 'Light Rain Mist', wucode: 'rain', day: 1, img: '9.png', luxpercent: 0.5],   // DAY: Light drizzle - Light Rain Mist
     [xucode: 1153, wuphrase: 'Rain Mist', wucode: 'rain', day: 1, img: '9.png', luxpercent: 0.5],   // DAY: Light drizzle - Rain Mist
     [xucode: 1168, wuphrase: 'Freezing Drizzle', wucode: 'sleet', day: 1, img: '8.png', luxpercent: 0.3],   // DAY: Freezing drizzle
     [xucode: 1168, wuphrase: 'Heavy Freezing Drizzle', wucode: 'sleet', day: 1, img: '6.png', luxpercent: 0.3],   // DAY: Freezing drizzle - Heavy Freezing Drizzle
     [xucode: 1168, wuphrase: 'Light Freezing Drizzle', wucode: 'sleet', day: 1, img: '8.png', luxpercent: 0.3],   // DAY: Freezing drizzle - Light Freezing Drizzle
     [xucode: 1171, wuphrase: 'Heavy Freezing Drizzle', wucode: 'sleet', day: 1, img: '6.png', luxpercent: 0.3],   // DAY: Heavy freezing drizzle
     [xucode: 1180, wuphrase: 'Light Rain', wucode: 'rain', day: 1, img: '11.png', luxpercent: 0.5],   // DAY: Patchy light rain - Light Rain
     [xucode: 1183, wuphrase: 'Heavy Mist', wucode: 'rain', day: 1, img: '11.png', luxpercent: 0.5],   // DAY: Light rain - Heavy Mist
     [xucode: 1183, wuphrase: 'Heavy Rain Mist', wucode: 'rain', day: 1, img: '11.png', luxpercent: 0.5],   // DAY: Light rain - Heavy Rain Mist
     [xucode: 1183, wuphrase: 'Light Rain', wucode: 'rain', day: 1, img: '11.png', luxpercent: 0.5],   // DAY: Light rain
     [xucode: 1186, wuphrase: 'Rain', wucode: 'rain', day: 1, img: '39.png', luxpercent: 0.5],   // DAY: Moderate rain at times - Rain
     [xucode: 1189, wuphrase: 'Heavy Drizzle', wucode: 'rain', day: 1, img: '5.png', luxpercent: 0.5],   // DAY: Moderate rain - Heavy Drizzle
     [xucode: 1189, wuphrase: 'Rain', wucode: 'rain', day: 1, img: '5.png', luxpercent: 0.5],   // DAY: Moderate rain - Rain
     [xucode: 1192, wuphrase: 'Heavy Rain', wucode: 'rain', day: 1, img: '40.png', luxpercent: 0.5],   // DAY: Heavy rain at times - Heavy Rain
     [xucode: 1195, wuphrase: 'Heavy Rain', wucode: 'rain', day: 1, img: '40.png', luxpercent: 0.5],   // DAY: Heavy rain
     [xucode: 1198, wuphrase: 'Light Freezing Rain', wucode: 'sleet', day: 1, img: '6.png', luxpercent: 0.3],   // DAY: Light freezing rain
     [xucode: 1201, wuphrase: 'Heavy Freezing Rain', wucode: 'rain', day: 1, img: '6.png', luxpercent: 0.5],   // DAY: Moderate or heavy freezing rain - Heavy Freezing Rain
     [xucode: 1204, wuphrase: 'Hail', wucode: 'sleet', day: 1, img: '35.png', luxpercent: 0.5],   // DAY: Light sleet - Hail
     [xucode: 1204, wuphrase: 'Light Hail', wucode: 'sleet', day: 1, img: '35.png', luxpercent: 0.5],   // DAY: Light sleet - Light Hail
     [xucode: 1204, wuphrase: 'Light Ice Crystals', wucode: 'sleet', day: 1, img: '25.png', luxpercent: 0.5],   // DAY: Light sleet - Light Ice Crystals
     [xucode: 1204, wuphrase: 'Light Ice Pellets', wucode: 'sleet', day: 1, img: '35.png', luxpercent: 0.5],   // DAY: Light sleet - Light Ice Pellets
     [xucode: 1204, wuphrase: 'Light Snow Grains', wucode: 'sleet', day: 1, img: '35.png', luxpercent: 0.5],   // DAY: Light sleet - Light Snow Grains
     [xucode: 1204, wuphrase: 'Small Hail', wucode: 'sleet', day: 1, img: '35.png', luxpercent: 0.5],   // DAY: Light sleet - Small Hail
     [xucode: 1207, wuphrase: 'Heavy Ice Crystals', wucode: 'sleet', day: 1, img: '25.png', luxpercent: 0.5],   // DAY: Moderate or heavy sleet - Heavy Ice Crystals
     [xucode: 1210, wuphrase: 'Light Snow', wucode: 'snow', day: 1, img: '13.png', luxpercent: 0.3],   // DAY: Patchy light snow - Light Snow
     [xucode: 1213, wuphrase: 'Light Snow', wucode: 'snow', day: 1, img: '14.png', luxpercent: 0.3],   // DAY: Light snow
     [xucode: 1219, wuphrase: 'Snow', wucode: 'snow', day: 1, img: '7.png', luxpercent: 0.3],   // DAY: Moderate snow - Snow
     [xucode: 1222, wuphrase: 'Heavy Snow', wucode: 'snow', day: 1, img: '41.png', luxpercent: 0.3],   // DAY: Patchy heavy snow - Heavy Snow
     [xucode: 1225, wuphrase: 'Heavy Snow', wucode: 'snow', day: 1, img: '41.png', luxpercent: 0.3],   // DAY: Heavy snow
     [xucode: 1237, wuphrase: 'Ice Crystals', wucode: 'sleet', day: 1, img: '17.png', luxpercent: 0.5],   // DAY: Ice pellets - Ice Crystals
     [xucode: 1237, wuphrase: 'Ice Pellets', wucode: 'sleet', day: 1, img: '17.png', luxpercent: 0.5],   // DAY: Ice pellets
     [xucode: 1237, wuphrase: 'Snow Grains', wucode: 'sleet', day: 1, img: '17.png', luxpercent: 0.5],   // DAY: Ice pellets - Snow Grains
     [xucode: 1240, wuphrase: 'Light Rain Showers', wucode: 'rain', day: 1, img: '10.png', luxpercent: 0.5],   // DAY: Light rain shower - Light Rain Showers
     [xucode: 1243, wuphrase: 'Heavy Rain Showers', wucode: 'rain', day: 1, img: '12.png', luxpercent: 0.5],   // DAY: Moderate or heavy rain shower - Heavy Rain Showers
     [xucode: 1243, wuphrase: 'Rain Showers', wucode: 'rain', day: 1, img: '12.png', luxpercent: 0.5],   // DAY: Moderate or heavy rain shower - Rain Showers
     [xucode: 1246, wuphrase: 'Heavy Rain Showers', wucode: 'rain', day: 1, img: '12.png', luxpercent: 0.5],   // DAY: Torrential rain shower - Heavy Rain Showers
     [xucode: 1249, wuphrase: 'Light Thunderstorms with Hail', wucode: 'sleet', day: 1, img: '5.png', luxpercent: 0.5],   // DAY: Light sleet showers - Light Thunderstorms with Hail
     [xucode: 1252, wuphrase: 'Freezing Rain', wucode: 'sleet', day: 1, img: '18.png', luxpercent: 0.5],   // DAY: Moderate or heavy sleet showers - Freezing Rain
     [xucode: 1252, wuphrase: 'Heavy Small Hail Showers', wucode: 'sleet', day: 1, img: '18.png', luxpercent: 0.5],   // DAY: Moderate or heavy sleet showers - Heavy Small Hail Showers
     [xucode: 1252, wuphrase: 'Heavy Snow Grains', wucode: 'sleet', day: 1, img: '18.png', luxpercent: 0.5],   // DAY: Moderate or heavy sleet showers - Heavy Snow Grains
     [xucode: 1252, wuphrase: 'Ice Pellet Showers', wucode: 'sleet', day: 1, img: '18.png', luxpercent: 0.5],   // DAY: Moderate or heavy sleet showers - Ice Pellet Showers
     [xucode: 1252, wuphrase: 'Small Hail Showers', wucode: 'sleet', day: 1, img: '18.png', luxpercent: 0.5],   // DAY: Moderate or heavy sleet showers - Small Hail Showers
     [xucode: 1255, wuphrase: 'Light Snow Showers', wucode: 'snow', day: 1, img: '16.png', luxpercent: 0.3],   // DAY: Light snow showers
     [xucode: 1258, wuphrase: 'Heavy Snow', wucode: 'snow', day: 1, img: '41.png', luxpercent: 0.3],   // DAY: Moderate or heavy snow showers - Heavy Snow
     [xucode: 1258, wuphrase: 'Heavy Snow Showers', wucode: 'snow', day: 1, img: '41.png', luxpercent: 0.3],   // DAY: Moderate or heavy snow showers - Heavy Snow Showers
     [xucode: 1258, wuphrase: 'Heavy Thunderstorms and Snow', wucode: 'snow', day: 1, img: '41.png', luxpercent: 0.3],   // DAY: Moderate or heavy snow showers - Heavy Thunderstorms and Snow
     [xucode: 1258, wuphrase: 'Snow Blowing Snow Mist', wucode: 'snow', day: 1, img: '41.png', luxpercent: 0.3],   // DAY: Moderate or heavy snow showers - Snow Blowing Snow Mist
     [xucode: 1258, wuphrase: 'Snow Showers', wucode: 'snow', day: 1, img: '41.png', luxpercent: 0.3],   // DAY: Moderate or heavy snow showers - Snow Showers
     [xucode: 1258, wuphrase: 'Thunderstorms and Ice Pellets', wucode: 'snow', day: 1, img: '41.png', luxpercent: 0.3],   // DAY: Moderate or heavy snow showers - Thunderstorms and Ice Pellets
     [xucode: 1258, wuphrase: 'Thunderstorms and Snow', wucode: 'snow', day: 1, img: '41.png', luxpercent: 0.3],   // DAY: Moderate or heavy snow showers - Thunderstorms and Snow
     [xucode: 1261, wuphrase: 'Light Hail Showers', wucode: 'snow', day: 1, img: '8.png', luxpercent: 0.3],   // DAY: Light showers of ice pellets - Light Hail Showers
     [xucode: 1261, wuphrase: 'Light Ice Pellet Showers', wucode: 'snow', day: 1, img: '8.png', luxpercent: 0.3],   // DAY: Light showers of ice pellets - Light Ice Pellet Showers
     [xucode: 1261, wuphrase: 'Light Small Hail Showers', wucode: 'snow', day: 1, img: '8.png', luxpercent: 0.3],   // DAY: Light showers of ice pellets - Light Small Hail Showers
     [xucode: 1261, wuphrase: 'Light Thunderstorms with Small Hail', wucode: 'snow', day: 1, img: '8.png', luxpercent: 0.3],   // DAY: Light showers of ice pellets - Light Thunderstorms with Small Hail
     [xucode: 1264, wuphrase: 'Hail Showers', wucode: 'sleet', day: 1, img: '4.png', luxpercent: 0.5],   // DAY: Moderate or heavy showers of ice pellets - Hail Showers
     [xucode: 1264, wuphrase: 'Heavy Hail', wucode: 'sleet', day: 1, img: '4.png', luxpercent: 0.5],   // DAY: Moderate or heavy showers of ice pellets - Heavy Hail
     [xucode: 1264, wuphrase: 'Heavy Hail Showers', wucode: 'sleet', day: 1, img: '4.png', luxpercent: 0.5],   // DAY: Moderate or heavy showers of ice pellets - Heavy Hail Showers
     [xucode: 1264, wuphrase: 'Heavy Ice Crystals', wucode: 'sleet', day: 1, img: '4.png', luxpercent: 0.5],   // DAY: Moderate or heavy showers of ice pellets - Heavy Ice Crystals
     [xucode: 1264, wuphrase: 'Heavy Ice Pellet Showers', wucode: 'sleet', day: 1, img: '4.png', luxpercent: 0.5],   // DAY: Moderate or heavy showers of ice pellets - Heavy Ice Pellet Showers
     [xucode: 1264, wuphrase: 'Heavy Ice Pellets', wucode: 'sleet', day: 1, img: '4.png', luxpercent: 0.5],   // DAY: Moderate or heavy showers of ice pellets - Heavy Ice Pellets
     [xucode: 1264, wuphrase: 'Heavy Thunderstorms and Ice Pellets', wucode: 'sleet', day: 1, img: '4.png', luxpercent: 0.5],   // DAY: Moderate or heavy showers of ice pellets - Heavy Thunderstorms and Ice Pellets
     [xucode: 1264, wuphrase: 'Heavy Thunderstorms with Hail', wucode: 'sleet', day: 1, img: '4.png', luxpercent: 0.5],   // DAY: Moderate or heavy showers of ice pellets - Heavy Thunderstorms with Hail
     [xucode: 1264, wuphrase: 'Heavy Thunderstorms with Small Hail', wucode: 'sleet', day: 1, img: '4.png', luxpercent: 0.5],   // DAY: Moderate or heavy showers of ice pellets - Heavy Thunderstorms with Small Hail
     [xucode: 1264, wuphrase: 'Thunderstorms with Small Hail', wucode: 'sleet', day: 1, img: '3.png', luxpercent: 0.3],   // DAY: Moderate or heavy showers of ice pellets - Thunderstorms with Small Hail
     [xucode: 1273, wuphrase: 'Light Thunderstorm', wucode: 'chancetstorms', day: 1, img: '37.png', luxpercent: 0.2],   // DAY: Patchy light rain with thunder - Light Thunderstorm
     [xucode: 1273, wuphrase: 'Light Thunderstorms and Rain', wucode: 'chancetstorms', day: 1, img: '37.png', luxpercent: 0.2],   // DAY: Patchy light rain with thunder - Light Thunderstorms and Rain
     [xucode: 1276, wuphrase: 'Heavy Thunderstorm', wucode: 'tstorms', day: 1, img: '3.png', luxpercent: 0.3],   // DAY: Moderate or heavy rain with thunder - Heavy Thunderstorm
     [xucode: 1276, wuphrase: 'Heavy Thunderstorms and Rain', wucode: 'tstorms', day: 1, img: '3.png', luxpercent: 0.3],   // DAY: Moderate or heavy rain with thunder - Heavy Thunderstorms and Rain
     [xucode: 1276, wuphrase: 'Thunderstorm', wucode: 'tstorms', day: 1, img: '3.png', luxpercent: 0.3],   // DAY: Moderate or heavy rain with thunder - Thunderstorm
     [xucode: 1276, wuphrase: 'Thunderstorms and Rain', wucode: 'tstorms', day: 1, img: '3.png', luxpercent: 0.3],   // DAY: Moderate or heavy rain with thunder - Thunderstorms and Rain
     [xucode: 1276, wuphrase: 'Thunderstorms with Hail', wucode: 'tstorms', day: 1, img: '3.png', luxpercent: 0.3],   // DAY: Moderate or heavy rain with thunder - Thunderstorms with Hail
     [xucode: 1279, wuphrase: 'Light Thunderstorms and Ice Pellets', wucode: 'chancesnow', day: 1, img: '41.png', luxpercent: 0.3],   // DAY: Patchy light snow with thunder - Light Thunderstorms and Ice Pellets
     [xucode: 1279, wuphrase: 'Light Thunderstorms and Snow', wucode: 'chancesnow', day: 1, img: '41.png', luxpercent: 0.3],   // DAY: Patchy light snow with thunder - Light Thunderstorms and Snow
     [xucode: 1282, wuphrase: 'Thunderstorms and Snow', wucode: 'snow', day: 1, img: '41.png', luxpercent: 0.3],   // DAY: Moderate or heavy snow with thunder - Thunderstorms and Snow
     [xucode: 1000, wuphrase: 'Breezy', wucode: 'breezy', day: 1, img: '22.png', luxpercent: 1],   // DAY: Breezy
     [xucode: 1000, wuphrase: 'Clear', wucode: 'nt_clear', day: 0, img: '31.png', luxpercent: 0],   // NIGHT: Clear
     [xucode: 1003, wuphrase: 'Partly Cloudy', wucode: 'nt_partlycloudy', day: 0, img: '29.png', luxpercent: 0],   // NIGHT: Partly cloudy
     [xucode: 1003, wuphrase: 'Scattered Clouds', wucode: 'nt_partlycloudy', day: 0, img: '29.png', luxpercent: 0],   // NIGHT: Partly cloudy - Scattered Clouds
     [xucode: 1006, wuphrase: 'Mostly Cloudy', wucode: 'nt_cloudy', day: 0, img: '26.png', luxpercent: 0],   // NIGHT: Cloudy - Mostly Cloudy
     [xucode: 1009, wuphrase: 'Overcast', wucode: 'nt_cloudy', day: 0, img: '27.png', luxpercent: 0],   // NIGHT: Overcast
     [xucode: 1030, wuphrase: 'Hazy', wucode: 'nt_hazy', day: 0, img: '21.png', luxpercent: 0],   // NIGHT: Mist
     [xucode: 1063, wuphrase: 'Rain', wucode: 'nt_rain', day: 0, img: '45.png', luxpercent: 0],   // NIGHT: Patchy rain possible - Rain
     [xucode: 1066, wuphrase: 'Light Thunderstorms and Snow', wucode: 'nt_chancesnow', day: 0, img: '46.png', luxpercent: 0],   // NIGHT: Patchy snow possible - Light Thunderstorms and Snow
     [xucode: 1069, wuphrase: 'Ice Pellets', wucode: 'nt_sleet', day: 0, img: '18.png', luxpercent: 0],   // NIGHT: Patchy sleet possible - Ice Pellets
     [xucode: 1072, wuphrase: 'Light Freezing Drizzle', wucode: 'nt_sleet', day: 0, img: '6.png', luxpercent: 0],   // NIGHT: Patchy freezing drizzle possible - Light Freezing Drizzle
     [xucode: 1087, wuphrase: 'Thunderstorm', wucode: 'nt_tstorms', day: 0, img: '38.png', luxpercent: 0],   // NIGHT: Thundery outbreaks possible - Thunderstorm
     [xucode: 1216, wuphrase: 'Snow', wucode: 'nt_snow', day: 0, img: '46.png', luxpercent: 0],   // NIGHT: Patchy moderate snow - Snow
	 [xucode: 1114, wuphrase: 'Blowing Snow', wucode: 'nt_snow', day: 0, img: '14.png', luxpercent: 0],   // NIGHT: Blowing snow
     [xucode: 1114, wuphrase: 'Heavy Blowing Snow', wucode: 'nt_snow', day: 0, img: '14.png', luxpercent: 0],   // NIGHT: Blowing snow - Heavy Blowing Snow
     [xucode: 1114, wuphrase: 'Heavy Low Drifting Snow', wucode: 'nt_snow', day: 0, img: '14.png', luxpercent: 0],   // NIGHT: Blowing snow - Heavy Low Drifting Snow
     [xucode: 1114, wuphrase: 'Heavy Snow Blowing Snow Mist', wucode: 'nt_snow', day: 0, img: '14.png', luxpercent: 0],   // NIGHT: Blowing snow - Heavy Snow Blowing Snow Mist
     [xucode: 1114, wuphrase: 'Light Blowing Snow', wucode: 'nt_snow', day: 0, img: '14.png', luxpercent: 0],   // NIGHT: Blowing snow - Light Blowing Snow
     [xucode: 1114, wuphrase: 'Light Low Drifting Snow', wucode: 'nt_snow', day: 0, img: '14.png', luxpercent: 0],   // NIGHT: Blowing snow - Light Low Drifting Snow
     [xucode: 1114, wuphrase: 'Light Snow Blowing Snow Mist', wucode: 'nt_snow', day: 0, img: '14.png', luxpercent: 0],   // NIGHT: Blowing snow - Light Snow Blowing Snow Mist
     [xucode: 1114, wuphrase: 'Low Drifting Snow', wucode: 'nt_snow', day: 0, img: '14.png', luxpercent: 0],   // NIGHT: Blowing snow - Low Drifting Snow
     [xucode: 1117, wuphrase: 'Heavy Snow', wucode: 'nt_snow', day: 0, img: '5.png', luxpercent: 0],   // NIGHT: Blizzard - Heavy Snow
     [xucode: 1135, wuphrase: 'Fog', wucode: 'nt_fog', day: 0, img: '20.png', luxpercent: 0],   // NIGHT: Fog
     [xucode: 1135, wuphrase: 'Fog Patches', wucode: 'nt_fog', day: 0, img: '20.png', luxpercent: 0],   // NIGHT: Fog - Fog Patches
     [xucode: 1135, wuphrase: 'Hazy', wucode: 'nt_fog', day: 0, img: '20.png', luxpercent: 0],   // NIGHT: Fog - Haze
     [xucode: 1135, wuphrase: 'Heavy Fog', wucode: 'nt_fog', day: 0, img: '20.png', luxpercent: 0],   // NIGHT: Fog - Heavy Fog
     [xucode: 1135, wuphrase: 'Heavy Fog Patches', wucode: 'nt_fog', day: 0, img: '20.png', luxpercent: 0],   // NIGHT: Fog - Heavy Fog Patches
     [xucode: 1135, wuphrase: 'Light Fog', wucode: 'nt_fog', day: 0, img: '20.png', luxpercent: 0],   // NIGHT: Fog - Light Fog
     [xucode: 1135, wuphrase: 'Light Fog Patches', wucode: 'nt_fog', day: 0, img: '20.png', luxpercent: 0],   // NIGHT: Fog - Light Fog Patches
     [xucode: 1135, wuphrase: 'Mist', wucode: 'nt_fog', day: 0, img: '20.png', luxpercent: 0],   // NIGHT: Fog - Mist
     [xucode: 1135, wuphrase: 'Partial Fog', wucode: 'nt_fog', day: 0, img: '20.png', luxpercent: 0],   // NIGHT: Fog - Partial Fog
     [xucode: 1135, wuphrase: 'Shallow Fog', wucode: 'nt_fog', day: 0, img: '20.png', luxpercent: 0],   // NIGHT: Fog - Shallow Fog
     [xucode: 1147, wuphrase: 'Freezing Fog', wucode: 'nt_fog', day: 0, img: '21.png', luxpercent: 0],   // NIGHT: Freezing fog
     [xucode: 1147, wuphrase: 'Heavy Freezing Fog', wucode: 'nt_fog', day: 0, img: '21.png', luxpercent: 0],   // NIGHT: Freezing fog - Heavy Freezing Fog
     [xucode: 1147, wuphrase: 'Light Freezing Fog', wucode: 'nt_fog', day: 0, img: '21.png', luxpercent: 0],   // NIGHT: Freezing fog - Light Freezing Fog
     [xucode: 1147, wuphrase: 'Patches of Fog', wucode: 'nt_fog', day: 0, img: '21.png', luxpercent: 0],   // NIGHT: Freezing fog - Patches of Fog
     [xucode: 1150, wuphrase: 'Light Drizzle', wucode: 'nt_rain', day: 0, img: '9.png', luxpercent: 0],   // NIGHT: Patchy light drizzle - Light Drizzle
     [xucode: 1153, wuphrase: 'Drizzle', wucode: 'nt_rain', day: 0, img: '9.png', luxpercent: 0],   // NIGHT: Light drizzle - Drizzle
     [xucode: 1153, wuphrase: 'Light Drizzle', wucode: 'nt_rain', day: 0, img: '9.png', luxpercent: 0],   // NIGHT: Light drizzle
     [xucode: 1153, wuphrase: 'Light Mist', wucode: 'nt_rain', day: 0, img: '9.png', luxpercent: 0],   // NIGHT: Light drizzle - Light Mist
     [xucode: 1153, wuphrase: 'Light Rain Mist', wucode: 'nt_rain', day: 0, img: '11.png', luxpercent: 0],   // NIGHT: Light drizzle - Light Rain Mist
     [xucode: 1153, wuphrase: 'Rain Mist', wucode: 'nt_rain', day: 0, img: '9.png', luxpercent: 0],   // NIGHT: Light drizzle - Rain Mist
     [xucode: 1168, wuphrase: 'Freezing Drizzle', wucode: 'nt_sleet', day: 0, img: '8.png', luxpercent: 0],   // NIGHT: Freezing drizzle
     [xucode: 1168, wuphrase: 'Light Freezing Drizzle', wucode: 'nt_sleet', day: 0, img: '8.png', luxpercent: 0],   // NIGHT: Freezing drizzle - Light Freezing Drizzle
     [xucode: 1171, wuphrase: 'Heavy Freezing Drizzle', wucode: 'nt_sleet', day: 0, img: '6.png', luxpercent: 0],   // NIGHT: Heavy freezing drizzle
     [xucode: 1180, wuphrase: 'Light Rain', wucode: 'nt_rain', day: 0, img: '11.png', luxpercent: 0],   // NIGHT: Patchy light rain - Light Rain
     [xucode: 1183, wuphrase: 'Heavy Mist', wucode: 'nt_rain', day: 0, img: '11.png', luxpercent: 0],   // NIGHT: Light rain - Heavy Mist
     [xucode: 1183, wuphrase: 'Heavy Rain Mist', wucode: 'nt_rain', day: 0, img: '11.png', luxpercent: 0],   // NIGHT: Light rain - Heavy Rain Mist
     [xucode: 1183, wuphrase: 'Light Rain', wucode: 'nt_rain', day: 0, img: '11.png', luxpercent: 0],   // NIGHT: Light rain
     [xucode: 1186, wuphrase: 'Rain', wucode: 'nt_rain', day: 0, img: '9.png', luxpercent: 0],   // NIGHT: Moderate rain at times - Rain
     [xucode: 1189, wuphrase: 'Heavy Drizzle', wucode: 'nt_rain', day: 0, img: '5.png', luxpercent: 0],   // NIGHT: Moderate rain - Heavy Drizzle
     [xucode: 1189, wuphrase: 'Rain', wucode: 'nt_rain', day: 0, img: '5.png', luxpercent: 0],   // NIGHT: Moderate rain - Rain
     [xucode: 1192, wuphrase: 'Heavy Rain', wucode: 'nt_rain', day: 0, img: '11.png', luxpercent: 0],   // NIGHT: Heavy rain at times - Heavy Rain
     [xucode: 1195, wuphrase: 'Heavy Rain', wucode: 'nt_rain', day: 0, img: '11.png', luxpercent: 0],   // NIGHT: Heavy rain
     [xucode: 1198, wuphrase: 'Light Freezing Rain', wucode: 'nt_sleet', day: 0, img: '6.png', luxpercent: 0],   // NIGHT: Light freezing rain
     [xucode: 1201, wuphrase: 'Heavy Freezing Rain', wucode: 'nt_rain', day: 0, img: '6.png', luxpercent: 0],   // NIGHT: Moderate or heavy freezing rain - Heavy Freezing Rain
     [xucode: 1204, wuphrase: 'Hail', wucode: 'nt_sleet', day: 0, img: '5.png', luxpercent: 0],   // NIGHT: Light sleet - Hail
     [xucode: 1204, wuphrase: 'Light Hail', wucode: 'nt_sleet', day: 0, img: '5.png', luxpercent: 0],   // NIGHT: Light sleet - Light Hail
     [xucode: 1204, wuphrase: 'Light Ice Crystals', wucode: 'nt_sleet', day: 0, img: '25.png', luxpercent: 0],   // NIGHT: Light sleet - Light Ice Crystals
     [xucode: 1204, wuphrase: 'Light Ice Pellets', wucode: 'nt_sleet', day: 0, img: '5.png', luxpercent: 0],   // NIGHT: Light sleet - Light Ice Pellets
     [xucode: 1204, wuphrase: 'Light Snow Grains', wucode: 'nt_sleet', day: 0, img: '5.png', luxpercent: 0],   // NIGHT: Light sleet - Light Snow Grains
     [xucode: 1204, wuphrase: 'Small Hail', wucode: 'nt_sleet', day: 0, img: '5.png', luxpercent: 0],   // NIGHT: Light sleet - Small Hail
     [xucode: 1207, wuphrase: 'Heavy Ice Crystals', wucode: 'nt_sleet', day: 0, img: '25.png', luxpercent: 0],   // NIGHT: Moderate or heavy sleet - Heavy Ice Crystals
     [xucode: 1210, wuphrase: 'Light Snow', wucode: 'nt_snow', day: 0, img: '13.png', luxpercent: 0],   // NIGHT: Patchy light snow - Light Snow
     [xucode: 1213, wuphrase: 'Light Snow', wucode: 'nt_snow', day: 0, img: '8.png', luxpercent: 0],   // NIGHT: Light snow
     [xucode: 1219, wuphrase: 'Snow', wucode: 'nt_snow', day: 0, img: '7.png', luxpercent: 0],   // NIGHT: Moderate snow - Snow
     [xucode: 1222, wuphrase: 'Heavy Snow', wucode: 'nt_snow', day: 0, img: '46.png', luxpercent: 0],   // NIGHT: Patchy heavy snow - Heavy Snow
     [xucode: 1225, wuphrase: 'Heavy Snow', wucode: 'snow', day: 0, img: '16.png', luxpercent: 0],   // NIGHT: Heavy snow
     [xucode: 1237, wuphrase: 'Ice Crystals', wucode: 'nt_sleet', day: 0, img: '16.png', luxpercent: 0],   // NIGHT: Ice pellets - Ice Crystals
     [xucode: 1237, wuphrase: 'Ice Pellets', wucode: 'nt_sleet', day: 0, img: '16.png', luxpercent: 0],   // NIGHT: Ice pellets
     [xucode: 1237, wuphrase: 'Snow Grains', wucode: 'nt_sleet', day: 0, img: '16.png', luxpercent: 0],   // NIGHT: Ice pellets - Snow Grains
     [xucode: 1240, wuphrase: 'Light Rain Showers', wucode: 'nt_rain', day: 0, img: '11.png', luxpercent: 0],   // NIGHT: Light rain shower - Light Rain Showers
     [xucode: 1243, wuphrase: 'Heavy Rain Showers', wucode: 'nt_rain', day: 0, img: '40.png', luxpercent: 0],   // NIGHT: Moderate or heavy rain shower - Heavy Rain Showers
     [xucode: 1243, wuphrase: 'Rain Showers', wucode: 'nt_rain', day: 0, img: '40.png', luxpercent: 0],   // NIGHT: Moderate or heavy rain shower - Rain Showers
     [xucode: 1246, wuphrase: 'Heavy Rain Showers', wucode: 'nt_rain', day: 0, img: '40.png', luxpercent: 0],   // NIGHT: Torrential rain shower - Heavy Rain Showers
     [xucode: 1249, wuphrase: 'Light Thunderstorms with Hail', wucode: 'nt_sleet', day: 0, img: '5.png', luxpercent: 0],   // NIGHT: Light sleet showers - Light Thunderstorms with Hail
     [xucode: 1252, wuphrase: 'Freezing Rain', wucode: 'nt_sleet', day: 0, img: '18.png', luxpercent: 0],   // NIGHT: Moderate or heavy sleet showers - Freezing Rain
     [xucode: 1252, wuphrase: 'Heavy Small Hail Showers', wucode: 'nt_sleet', day: 0, img: '18.png', luxpercent: 0],   // NIGHT: Moderate or heavy sleet showers - Heavy Small Hail Showers
     [xucode: 1252, wuphrase: 'Heavy Snow Grains', wucode: 'nt_sleet', day: 0, img: '18.png', luxpercent: 0],   // NIGHT: Moderate or heavy sleet showers - Heavy Snow Grains
     [xucode: 1252, wuphrase: 'Ice Pellet Showers', wucode: 'nt_sleet', day: 0, img: '18.png', luxpercent: 0],   // NIGHT: Moderate or heavy sleet showers - Ice Pellet Showers
     [xucode: 1252, wuphrase: 'Small Hail Showers', wucode: 'nt_sleet', day: 0, img: '18.png', luxpercent: 0],   // NIGHT: Moderate or heavy sleet showers - Small Hail Showers
     [xucode: 1255, wuphrase: 'Light Snow Showers', wucode: 'nt_snow', day: 0, img: '16.png', luxpercent: 0],   // NIGHT: Light snow showers
     [xucode: 1258, wuphrase: 'Heavy Snow', wucode: 'nt_snow', day: 0, img: '42.png', luxpercent: 0],   // NIGHT: Moderate or heavy snow showers - Heavy Snow
     [xucode: 1258, wuphrase: 'Heavy Snow Showers', wucode: 'nt_snow', day: 0, img: '42.png', luxpercent: 0],   // NIGHT: Moderate or heavy snow showers - Heavy Snow Showers
     [xucode: 1258, wuphrase: 'Snow Blowing Snow Mist', wucode: 'nt_snow', day: 0, img: '41.png', luxpercent: 0],   // NIGHT: Moderate or heavy snow showers - Snow Blowing Snow Mist
     [xucode: 1258, wuphrase: 'Snow Showers', wucode: 'nt_snow', day: 0, img: '41.png', luxpercent: 0],   // NIGHT: Moderate or heavy snow showers - Snow Showers
     [xucode: 1261, wuphrase: 'Light Hail Showers', wucode: 'nt_snow', day: 0, img: '8.png', luxpercent: 0],   // NIGHT: Light showers of ice pellets - Light Hail Showers
     [xucode: 1261, wuphrase: 'Light Ice Pellet Showers', wucode: 'nt_snow', day: 0, img: '8.png', luxpercent: 0],   // NIGHT: Light showers of ice pellets - Light Ice Pellet Showers
     [xucode: 1261, wuphrase: 'Light Small Hail Showers', wucode: 'nt_snow', day: 0, img: '8.png', luxpercent: 0],   // NIGHT: Light showers of ice pellets - Light Small Hail Showers
     [xucode: 1261, wuphrase: 'Light Thunderstorms with Small Hail', wucode: 'nt_snow', day: 0, img: '8.png', luxpercent: 0],   // NIGHT: Light showers of ice pellets - Light Thunderstorms with Small Hail
     [xucode: 1264, wuphrase: 'Hail Showers', wucode: 'nt_sleet', day: 0, img: '3.png', luxpercent: 0],   // NIGHT: Moderate or heavy showers of ice pellets - Hail Showers
     [xucode: 1264, wuphrase: 'Heavy Hail', wucode: 'nt_sleet', day: 0, img: '3.png', luxpercent: 0],   // NIGHT: Moderate or heavy showers of ice pellets - Heavy Hail
     [xucode: 1264, wuphrase: 'Heavy Hail Showers', wucode: 'nt_sleet', day: 0, img: '3.png', luxpercent: 0],   // NIGHT: Moderate or heavy showers of ice pellets - Heavy Hail Showers
     [xucode: 1264, wuphrase: 'Heavy Ice Crystals', wucode: 'nt_sleet', day: 0, img: '3.png', luxpercent: 0],   // NIGHT: Moderate or heavy showers of ice pellets - Heavy Ice Crystals
     [xucode: 1264, wuphrase: 'Heavy Ice Pellet Showers', wucode: 'nt_sleet', day: 0, img: '3.png', luxpercent: 0],   // NIGHT: Moderate or heavy showers of ice pellets - Heavy Ice Pellet Showers
     [xucode: 1264, wuphrase: 'Heavy Ice Pellets', wucode: 'nt_sleet', day: 0, img: '3.png', luxpercent: 0],   // NIGHT: Moderate or heavy showers of ice pellets - Heavy Ice Pellets
     [xucode: 1264, wuphrase: 'Heavy Thunderstorms and Ice Pellets', wucode: 'nt_sleet', day: 0, img: '3.png', luxpercent: 0],   // NIGHT: Moderate or heavy showers of ice pellets - Heavy Thunderstorms and Ice Pellets
     [xucode: 1264, wuphrase: 'Heavy Thunderstorms with Hail', wucode: 'nt_sleet', day: 0, img: '3.png', luxpercent: 0],   // NIGHT: Moderate or heavy showers of ice pellets - Heavy Thunderstorms with Hail
     [xucode: 1264, wuphrase: 'Heavy Thunderstorms with Small Hail', wucode: 'nt_sleet', day: 0, img: '3.png', luxpercent: 0],   // NIGHT: Moderate or heavy showers of ice pellets - Heavy Thunderstorms with Small Hail
     [xucode: 1264, wuphrase: 'Thunderstorms with Small Hail', wucode: 'nt_sleet', day: 0, img: '3.png', luxpercent: 0],   // NIGHT: Moderate or heavy showers of ice pellets - Thunderstorms with Small Hail
     [xucode: 1273, wuphrase: 'Light Thunderstorm', wucode: 'nt_chancetstorms', day: 0, img: '47.png', luxpercent: 0],   // NIGHT: Patchy light rain with thunder - Light Thunderstorm
     [xucode: 1273, wuphrase: 'Light Thunderstorms and Rain', wucode: 'nt_chancetstorms', day: 0, img: '47.png', luxpercent: 0],   // NIGHT: Patchy light rain with thunder - Light Thunderstorms and Rain
     [xucode: 1276, wuphrase: 'Heavy Thunderstorm', wucode: 'nt_tstorms', day: 0, img: '38.png', luxpercent: 0],   // NIGHT: Moderate or heavy rain with thunder - Heavy Thunderstorm
     [xucode: 1276, wuphrase: 'Heavy Thunderstorms and Rain', wucode: 'nt_tstorms', day: 0, img: '38.png', luxpercent: 0],   // NIGHT: Moderate or heavy rain with thunder - Heavy Thunderstorms and Rain
     [xucode: 1276, wuphrase: 'Thunderstorm', wucode: 'nt_tstorms', day: 0, img: '38.png', luxpercent: 0],   // NIGHT: Moderate or heavy rain with thunder - Thunderstorm
     [xucode: 1276, wuphrase: 'Thunderstorms and Rain', wucode: 'nt_tstorms', day: 0, img: '38.png', luxpercent: 0],   // NIGHT: Moderate or heavy rain with thunder - Thunderstorms and Rain
     [xucode: 1276, wuphrase: 'Thunderstorms with Hail', wucode: 'nt_tstorms', day: 0, img: '38.png', luxpercent: 0],   // NIGHT: Moderate or heavy rain with thunder - Thunderstorms with Hail
     [xucode: 1279, wuphrase: 'Light Thunderstorms and Ice Pellets', wucode: 'nt_chancesnow', day: 0, img: '41.png', luxpercent: 0],   // NIGHT: Patchy light snow with thunder - Light Thunderstorms and Ice Pellets
     [xucode: 1279, wuphrase: 'Light Thunderstorms and Snow', wucode: 'nt_chancesnow', day: 0, img: '41.png', luxpercent: 0],   // NIGHT: Patchy light snow with thunder - Light Thunderstorms and Snow
     [xucode: 1282, wuphrase: 'Heavy Thunderstorms and Snow', wucode: 'nt_snow', day: 0, img: '18.png', luxpercent: 0],   // NIGHT: Moderate or heavy snow with thunder - Heavy Thunderstorms and Snow
     [xucode: 1282, wuphrase: 'Thunderstorms and Ice Pellets', wucode: 'nt_snow', day: 0, img: '18.png', luxpercent: 0],   // NIGHT: Moderate or heavy snow with thunder - Thunderstorms and Ice Pellets
     [xucode: 1282, wuphrase: 'Thunderstorms and Snow', wucode: 'nt_snow', day: 0, img: '18.png', luxpercent: 0],   // NIGHT: Moderate or heavy snow with thunder - Thunderstorms and Snow
     [xucode: 1000, wuphrase: 'Breezy', wucode: 'nt_breezy', day: 0, img: '23.png', luxpercent: 0],   // NIGHT: Breezy
]    
//******************************************************************************************