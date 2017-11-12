var moment = require("moment");
moment.locale('en-gb');
module.exports = function(controller) {

    controller.hears([/^prescribe/], 'direct_message,direct_mention', function(bot, message) {
        var regEx = /prescribe (.*?) to (.*?) every (.*?) hours until ((0?[1-9]|[12][0-9]|3[01])[\/\-](0?[1-9]|1[012])[\/\-]\d{4})/;
        var matches = regEx.exec(message.text);
        var commands = {
            "medication": matches[1],
            "userEmail": matches[2],
            "hoursBetweenDosis": matches[3],
            "endDate": matches[4]
        };
        bot.reply(message, "Okay. I'll remind " + commands.userEmail + " to take " + commands.medication + " every " + commands.hoursBetweenDosis + " hours until " + commands.endDate);

        commands.intervalId = setInterval(function() {
            require('../bot').createMessage("You should take **" + commands.medication + "**", commands.userEmail);
            var today = moment().format('L');
            if (today === moment(commands.endDate).format('L')) clearInterval(commands.intervalId);
        }, commands.hoursBetweenDosis * 3600 * 1000, commands);

        createPrescription(commands);
    });
};

function createPrescription(commands) {
    require('../bot').bot.prescriptions.push(commands);
}
