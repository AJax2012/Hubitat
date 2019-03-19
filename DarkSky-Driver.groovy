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
    definition (name: "AJax2012 DarkSky Driver", namespace: "thisone", author: "Adam Gardner") {
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
    }

    preferences() {
        section("Query Inputs"){
            input "apiKey", "text", required: true, title: "API Key";
            input "lat", "text", required: true, title: "Latitude";
            input "lng", "text", required: true, title: "Longitude";
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

def updated() {
    log.debug "updated called"
    updateCheck();
    version();
    state.NumOfPolls = 0;
    ForcePoll();
    def pollIntervalCmd = (settings?.pollInterval ?: "5 Minutes").replace(" ", "");
    if(autoPoll)
        "runEvery${pollIntervalCmd}"(pollSchedule);
    
    def changeOver = cutOff;
    schedule(changeOver, ResetPollCount);
}

def ResetPollCount(){
    state.NumOfPolls = -1;
    log.info "Poll counter reset..";
    ForcePoll();
}

def pollSchedule()
{
    ForcePoll();
}
              
def parse(String description) {
}

def poll()
{
    if(now() - state.lastPoll > (pollIntervalLimit * 60000))
        ForcePoll();
    else
        log.debug "Poll called before interval threshold was reached";
}

def ForcePoll()
{
    // state.NumOfPolls += 1;
    // log.info " state.NumOfPolls = $state.NumOfPolls";
   
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
            sendEvent(name: "weatherIcon", value: current.icon, isStateChange: true);
            sendEvent(name: "forecastIcon", value: tomorrow.icon, isStateChange: true);
            
            def distanceMeasurement = "";
            def speedMeasurement = "";

            def builder = new StringBuilder();
            
            // start summary string builder section
            if(summaryType == true) {
            
                if (WeatherSummeryFormat == "Celsius, Miles & MPH"){
                    sendEvent(name: "weatherSummaryFormat", value: "Celsius, Miles & MPH", isStateChange: true)
                    distanceMeasurement = "Miles";
                    speedMeasurement = "mph";
                }
                    
                if (WeatherSummeryFormat == "Fahrenheit, Miles & MPH"){
                    sendEvent(name: "weatherSummaryFormat", value: "Fahrenheit, Miles & MPH", isStateChange: true)
                    distanceMeasurement = "Miles";
                    speedMeasurement = "mph";
                }
                    
                if (WeatherSummeryFormat == "Celsius, Kilometres & KPH"){
                    sendEvent(name: "weatherSummaryFormat", value: "Celsius, Kilometres & KPH", isStateChange: true)
                    distanceMeasurement = "Kilometres";
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
                builder.append(windSpeed);
                builder.append(" ");
                builder.append(speedMeasurement);
                builder.append(", with gusts up to ");
                builder.append(windGust);
                builder.append(speedMeasurement);
                builder.append(". Visibility is around ");
                builder.append(visibility);
                builder.append(distanceMeasurement);
                builder.append(". There is a ");
                builder.append(todayPrecipProbability);
                builder.append("% chance of ");
                builder.append(precipType);
                builder.append(" today.");

                sendEvent(name: "weatherSummary", value: builder.toString(), isStateChange: true);
            }
            
            // start short summary string builder section
            if(summaryType == false){
                
                if (WeatherSummeryFormat == "Celsius, Miles & MPH"){
                    sendEvent(name: "weatherSummaryFormat", value: "Celsius, Miles & MPH", isStateChange: true)
                    distanceMeasurement = "Miles";
                    speedMeasurement = "mph";
                }
            
                if (WeatherSummeryFormat == "Fahrenheit, Miles & MPH"){
                    sendEvent(name: "weatherSummaryFormat", value: "Fahrenheit, Miles & MPH", isStateChange: true)
                    distanceMeasurement = "Miles";
                    speedMeasurement = "mph";
                }
            
                if (WeatherSummeryFormat ==  "Celsius, Kilometres & KPH"){
                    sendEvent(name: "weatherSummaryFormat", value:  "Celsius, Kilometres & KPH", isStateChange: true)
                    distanceMeasurement = "Kilometres";
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
                builder.append(windSpeed);
                builder.append(speedMeasurement);
                builder.append(". Gust: ");
                builder.append(windGust);
                builder.append(speedMeasurement);
                builder.append(". ");
                builder.append(precipType);
                builder.append(": ");
                builder.append(todayPrecipProbability);
                builder.append("%");

                sendEvent(name: "weatherSummary", value: builder.toString(), isStateChange: true);
            }
                    
            
        }
    } catch (e) {
        log.error "something went wrong: $e";
    }
}

def Report(){
    def obvTime = observationTime.value
    log.info "$obvTime"  
}


def version(){
    updateCheck();
    schedule("0 0 9 ? * FRI *", updateCheck())
}
    

def updateCheck(){
    setVersion();
	def paramsUD = [uri: "http://update.hubitat.uk/json/${state.CobraAppCheck}"];
    try {
        httpGet(paramsUD) { respUD ->
//  log.warn " Version Checking - Response Data: ${respUD.data}"   // Troubleshooting Debug Code **********************
            def copyrightRead = (respUD.data.copyright)
            state.Copyright = copyrightRead
            def newVerRaw = (respUD.data.versions.Driver.(state.InternalName))
    //		log.warn "$state.InternalName = $newVerRaw"
            def newVer = newVerRaw.replace(".", "")
//			log.warn "$state.InternalName = $newVer"
            def currentVer = state.version.replace(".", "")
            state.UpdateInfo = "Updated: "+ state.newUpdateDate + " - "+ (respUD.data.versions.UpdateInfo.Driver.(state.InternalName))
            state.author = (respUD.data.author)
            state.newUpdateDate = (respUD.data.Comment)
        
            if(newVer == "NLS"){
                state.Status = "<b>** This driver is no longer supported by $state.author  **</b>"       
                log.warn "** This driver is no longer supported by $state.author **"      
            }           
            else if(currentVer < newVer){
                state.Status = "<b>New Version Available (Version: $newVerRaw)</b>"
                log.warn "** There is a newer version of this driver available  (Version: $newVerRaw) **"
                log.warn "** $state.UpdateInfo **"
            } 
            else{ 
                state.Status = "Current"
                log.info "You are using the current version of this driver"
            }
        }
    } 
    catch (e) {
        log.error "Something went wrong: CHECK THE JSON FILE AND IT'S URI -  $e"
        }
    if(state.Status == "Current"){
        state.UpdateInfo = "N/A"
        sendEvent(name: "DriverUpdate", value: state.UpdateInfo, isStateChange: true)
        sendEvent(name: "DriverStatus", value: state.Status, isStateChange: true)
        }
    else{
        sendEvent(name: "DriverUpdate", value: state.UpdateInfo, isStateChange: true)
        sendEvent(name: "DriverStatus", value: state.Status, isStateChange: true)
    }   
    sendEvent(name: "DriverAuthor", value: state.author, isStateChange: true)
    sendEvent(name: "DriverVersion", value: state.version, isStateChange: true)
}

def setVersion(){
    state.version = "1.0.0"
    state.InternalName = "AJax2012 DarkSky Weather"
   	state.CobraAppCheck = "customWeather.json"
    sendEvent(name: "DriverAuthor", value: "Adam Gardner", isStateChange: true)
    sendEvent(name: "DriverVersion", value: state.version, isStateChange: true)
}
