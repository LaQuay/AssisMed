module.exports = function(controller) {

    controller.hears([/^medicalevent$/], 'direct_message,direct_mention', function(bot, message) {

        bot.startConversation(message, function(err, convo) {
            var data = {};

            // Creates a new Medical Exam
            // response.text format = "new (eventname) for (pacientemail) on (date) at (hour)"
            convo.ask('What is the medical event?', function(response, convo) {
                var regEx = /new (.*?) for (.*?) on ([0-3]?[0-9]\/[0-1]?[0-9]\/20[0-9][0-9]) at ((?:[0-1]?[0-9]|2[0-3]):[0-5][0-9])$/;
                var sentence = response.text;
                var match = regEx.exec(sentence);

                data.event = match[1];
                data.patient = match[2];
                data.day = match[3];
                data.hour = match[4];

                convo.say("We have recorded a new event for **" + data.patient + "** - " + data.event + " (" + data.day + " " + data.hour + ")");

                var moment = require("moment");

                var timeNow = new Date();
                var timeRemainder = moment(data.day + " " + data.hour, "DD/MM/YYYY H:mm");
                var time = (timeRemainder - timeNow);

                var timeBefore = 24 * 60 * 60 * 1000;
                time -= timeBefore;
                time -= 1 * 60 * 60 * 1000;

                var callback = function(bot, data, https) {
                    var message = "Remember your medical exam: **" + data.event + "** on **" + data.day + "** at **" + data.hour + "**";
                    require('../bot').createMessage(message, data.patient);
                };
                setTimeout(callback, time, require('../bot').bot, data, require('https'));

                createVisit(data);

                convo.next();
            });
        });

    });
};

function createVisit(data) {
    require('../bot').bot.visits.push(data);
}
