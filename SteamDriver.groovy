metadata {
    definition (name: "Steam API Driver", namespace: "AJax2012", author: "Adam Gardner") {
        capability "Motion Sensor"
        capability "Switch";
        capability "Sensor";
        command "ForcePoll";
        command "poll";

        attribute "userName", "string";
        attribute "avatar", "string";
        attribute "status", "string";
        attribute "currentGame", "string";
 		attribute "DriverAuthor", "string";
    }

    preferences() {
        section("Query Inputs"){
            input "apiKey", "text", required: true, title: "API Key";
            input "steamId", "text", required: true, title: "steamID64 (https://steamidfinder.com/lookup)";
            input "autoPoll", "bool", required: false, title: "Enable Auto Poll";
            input "pollInterval", "enum", title: "Auto Poll Interval:", required: false, options: ["1 Second","5 Seconds", "10 Seconds", "15 Seconds", "30 Seconds", "1 Minute", "3 Minutes"];
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
    log.debug "Steam Forcepoll called";
    def params = [uri: "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=${apiKey}&steamids=${steamId}"];

    try {
        httpGet(params) { resp ->

            // resp.headers.each {
            //     log.debug "Response: ${it.name} : ${it.value}"
            // }

            // log.debug "params: ${params}"
            // log.debug "response contentType: ${resp.contentType}"
            // log.debug "response data: ${resp.data}"

            def data = resp.data.response.players[0]

            sendEvent(name: "userName", value: data.personaname, isStateChange: true);
            sendEvent(name: "avatar", value: data.avatar, isStateChange: true);

            if (data.gameextrainfo){
                sendEvent(name: "currentGame", value: data.gameextrainfo, isStateChange: true);
                sendEvent(name: "status", value: "in game", unit: rainUnit, isStateChange: true);
                sendEvent(name: "switch", value: "on");
                sendEvent(name: "motion", value: "on");
            }
            else {
                sendEvent(name: "currentGame", value: "n/a", isStateChange: true);
                sendEvent(name: "status", value: "offline", unit: rainUnit, isStateChange: true);
                sendEvent(name: "switch", value: "off")
                sendEvent(name: "motion", value: "off");
            }
        }
    } catch (e) {
        log.debug e
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
    state.InternalName = "Steam API Driver"
    sendEvent(name: "DriverAuthor", value: "Adam Gardner", isStateChange: true)
}