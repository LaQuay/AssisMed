module.exports = function(controller) {

    controller.hears([/^visits of (.*?)$/], 'direct_message,direct_mention', function(bot, message) {
        var matches = message.text.split(" ");
        var commands = {
            "userEmail": matches[2],
        };
        var visits = []
        require('../bot').bot.visits.forEach(function(visit) {
            if (visit.patient === commands.userEmail) visits.push(visit);
        });
        var text;
        if (visits.length === 0) {
            text = "There are no planned visits for " + commands.userEmail;
        }
        else {
            text = "Here are the planned visits of " + commands.userEmail + ":";
            require('../bot').bot.visits.forEach(function(visit) {
                text += "\n- **" + visit.event + "** - (" + visit.day + " " + visit.hour + ")";
            });
        }
        bot.reply(message, text);
    });
}
