# AssisMed

## Inspiration
Have you ever visited the doctor while feeling sick? It is known that he/she won't so far the time we would expect, he/she is going to prescribe some medication that will probably look like a hieroglyph which we won't able to understand.

![prescription](http://3.bp.blogspot.com/-Ny64A7Alwng/TmX6ZRP_6ZI/AAAAAAAAGBc/LLVZUphb90c/s1600/imagesCAQLBVJ2.jpg)

And we will be forced to go to the pharmacy to buy a treatment that's probably sold out or not in stock.

These are some of the reasons from where **AssistMed** comes from. The idea that patient's experience should be as easy as talking to a friend is.

## What it does

AssistMed has two different approaches.

- It **creates a bot** for Cisco Spark Chat that adds **four** health-related features. Using it, **doctors can prescribe medication to its patients and automatically set reminders through the chat**. Moreover, doctors/secretaries can organise visits and medical events for organize patients agenda and also automatically remind them when the event is near. This for options are easy usable for non technical users because they resemble natural language.
  - `medicalevent`: Asks you to create a new event in patients agenda using the following format `new <event> for <email> on <date> at <time>`.
  - `visits of <email>`: List all the events in user's agenda.
  - `prescribe <what> to <email> every <amount_of_hours> hours until <date>`: Prescibe a medication to the user and automatically sets a reminder to the user.
  - `prescriptions of <email>`: List all the prescriptions in the user reminders.

- A **user-friendly android mobile application** that integrates with Cisco Spark Chat by **providing a chat and a videoconference between patient and his/her doctor**.

  - `Authentication`: Create a new Spark instance using Spark ID authentication (OAuth-based).
  - `Register phone`: Register the device to send and receive calls.
  - `Call`: Send calls (video enabled).
  - `Hangup call`: Hangup a call.
  - `Listener for receiving calls`: Set up a listener in order to receive a call (video enabled).
  - `Post Message`: Post a Message to an specífic room or a person.
  - `Read Messages`: Read all Messages of a room.

## How we built it

We created a board using Cisco Spark to reproduce a hospital with professionals and patients. In it we created a bot named HealthAssistBot that will be the link between both type of people.

When the professional needs to create a new event for the patient, he ask the bot to create that event and a reminder for the patient. Then, when the date of the event is close to the present, the bot will send a message with the information of the event.

Also, the videoconferences are built using the Cisco Spark Meetings to connect the patient with a professional.

**All this options can be performed by both a web and an Android app.**

## Challenges we ran into

One of the principal challenges was the integration with Cisco Spark platform in the Web side, it is really good documented but it has a lot of functionalities so it becomes a big lecture. Also, people from Cisco helped us to understand how their tools work.

But **the main challenge** was in Android. Developing a native Android app for Cisco Spark whilst the Cisco Spark lib is still in beta and undocumented its no so easy.

## Accomplishments that we're proud of

We have built an Android native application that integrates with Cisco Spark system using a beta SDK version. That brought several issues that we are proud to have reported to the staff and the developers team.

However, using Cisco Spark API (For web and bot) has been easy and a pleasure to use it and extend it.


## What we learned

We build our first bot, and the most amazing thing is that it works! Also, we distribute coding tasks trying to each member of the group use a new technology for him. Finally we learnt about Cisco Spark and its collaborative possibilities.

## What's next for AssistMed

Some fantastic ideas turn up during the hack but for time limitations they became stored. We thought integrating with pharmacies to notify them when a patient requires a medicine and preorder if it is sold out. Moreover, we want to improve preoperative and postoperative care of the patient giving indications.
