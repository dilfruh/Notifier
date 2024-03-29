# Description
This is an Android app (minimum Android 10) that gives custom vibration patterns to certain notifications. Finally, you can differentiate between make Facebook notify you with 2 short buzzes, and Gmail with 3 long ones. You can also use it for custom edge lighting on some Samsung devices. This was originally created in around 2017, so forgive any poor coding decisions on my part, haha.

## Instructions (also found in the app)

1. Give the Notifier access to notifications by clicking the Give Notification Access button.
2. If you have edge lighting, go into it in your phone settings, turn on Notifier, and add Keywords that are the app names sending the notifications you want lit up and choose the color you want.
3. Back in Notifier, type the app name, choose a vibration pattern (which you can test out), and select if you want it to use edge lighting when the screen is off (if you don't have edge lighting on your phone, this will wake the screen). Then hit Create/Edit.
4. Turn off vibration for the other apps you want to replace vibration patterns. Then set their sound to silent because even if you have vibrate off, it will convert a sound to a vibration if your phone is in vibrate mode (but keep the notification importance on Alert so you can still get the popup). Then you should set your sound in the notification channels in Notifier. Yes you lose individual different sounds for different channels in the same app :(
5. To delete an existing app from your list, just hit the trash icon next to it. To edit one, do the same thing as you would for creating.

### Notes:
1. The app name should be exactly as it appears below the app's icon on your home screen/app drawer.
2. You need popup on when screen is off for you to get edge lighting.
3. You'll have to change the sound again every time you edit a notification in Notifier.
4. Choose none for vibration pattern if you just want to add edge lighting and not change the vibration. Messages is a good app to choose for this because you might want to have different sounds for different people, and unfortunately this app can't do that :(
5. Sometimes Android kills off apps' services, so if you noticed you stopped receiving notifications, just reopen the app. You probably want to make sure the app doesn't go to sleep and isn't optimized for battery life in your phone's settings to help prevent this.
6. I tried to get it not to notify for repeat notifications, but it still might notify when you think it shouldn't. Believe me, apps send out a bunch of duplicates, we just don't see them.
7. What are notification channels? If you go into your phone's settings and click on the app you're looking at, you'll see different categories for notifications. These are notification channels. To a certain extent you can customize different types of notifications, like their sound, if they vibrate, or if they popup on your screen when they arrive.
8. I don't read or do anything with your notifications. I just created this app for myself and then decided to share it with others. So if you wanna create it on your own like I did to make sure you're safe, I'll help by telling you a little more on how it works. For each app I create a channel that's used when the screen is on. It has no popup so it doesn't interfere with the helpful popups you already get. All this channel does is use the custom vibration. If you don't want edge lighting I basically create the same channel again to be used when the screen is off. However, if you want edge lighting (or just wake the screen), I create another channel to be used when the screen is off that has a phrase in it's id name so if it's used the app will check for that phrase in the channel id and will wake the screen when it sends the notification, which is the only way to activate edge lighting on some phones for some dumb reason. This channel needs popup on because that's the only way edge lighting will be able to read the keywords in its title and change the color accordingly. Also you wanna see a little more info from the notification right? My notification listener service checks every notification that comes in, gets the app name, and checks if I have a channel set up for that app name, and if so will use it and send a notification that has the app name in the title, and in the content text it will have the original notification's title and content text. You need the app name in the title because the only way edge lighting color can differ using the same app is if you use a different keyword in the title. Then I automatically dismiss it after 5 sec so it doesn't distract you too much but also has enough time to finish its vibration. Anyway, enjoy!

### Here is a screenshot
![Screenshot_20240208_152409_Notifier](https://github.com/dilfruh/Notifier/assets/158101348/ffa88098-543b-41d7-a067-252b5cd87402)



