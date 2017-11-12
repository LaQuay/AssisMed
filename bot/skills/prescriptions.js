module.exports = function(controller) {

    controller.hears([/^prescriptions of (.*?)$/], 'direct_message,direct_mention', function(bot, message) {
        console.log(message.text);
        var matches = message.text.split(" ");
        console.log(matches);
        var commands = {
            "userEmail": matches[2],
        };
        var prescriptions = []
        require('../bot').bot.prescriptions.forEach(function(prescription) {
            if (prescription.userEmail === commands.userEmail) {
                prescriptions.push(prescription);
            }
        });
        var text = '';
        if (prescriptions.length === 0) {
            text = "There is no prescription for " + commands.userEmail;
        }
        else {
            text = "Here are the prescriptions for " + commands.userEmail + ":";
            prescriptions.forEach(function(prescription) {
                text += "\n- **" + prescription.medication + "** every **" + prescription.hoursBetweenDosis + " hours** until **" + prescription.endDate + "**";
            });
        }
        bot.reply(message, text);
    });
};
