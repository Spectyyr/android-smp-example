SessionM Platform SDK Example Apps
===================

Overview
------
Included in this repo are example modules that show how to use the SessionM Platform SDK for Android. Follow the steps below to setup and build a project:

- Clone this repo.
- Open settings.gradle in Android Studio.

You should be able to run different modules with default demo server and app.

If you want to change the APP key and server as your custom ones, make the following changes:

- Add your SessionM App API key in res/values/Strings.xml in "app_key" field.
- set your server url in SEApplication.java: sessionM.setServerType(SessionM.SERVER_TYPE_CUSTOM, "PLEASE_SET_YOUR_CUSTOM_SERVER_HERE");

For more SessionM Platform features, please email us directly.

LICENSE: MIT

For more help see https://mmc.sessionm.com/docs/mmc-sdk/

## Table of Contents  

[Campaigns](#Campaigns)

[Contents](#Contents)

[Geofence](#Geofence)

[Inbox](#Inbox)

[Loyalty Cards](#Loyalty_Cards)

[Places](#Places)

[Push Notification](#Push_Notification)

[Receipts](#Receipts)

[Referrals](#Referrals)

[Rewards](#Rewards)

[Transactions](#Transactions)


<a name="Campaigns"/>

## Campaigns

This app showcases how to use SessionM SDK to fetch promotional campaign messages for presentation in an activity feed.

API docs: https://mmc.sessionm.com/docs/mmc-sdk/#campaigns

By default it uses the anonymous user. You can click on “Click Here To Login User” to login the default sample user to see the customized activity feed.

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/campaigns_switch_user.gif" alt="CampaignsSwitchUser Anima" width="324" height="576" />

A message tile can be clicked to showcase its associated action (e.g. presenting an ad or opening a web page in native browser).

There are three different actions to trigger different types of feed message: Open ad, deep link and external link. Click on any of the tiles gives you different actions:

- Open Ad: Opens a pre-set Ad in the app’s portal.

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/campaigns_open_ad.gif" alt="CampaignsOpenAd Anima" width="324" height="576" />

- Deep Link: Opens a dialog with a sample deep link schema

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/campaigns_deep_link.gif" alt="CampaignsDeepLink Anima" width="324" height="576" />

- External Link: Opens native browser with sample URL

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/campaigns_external_link.gif" alt="CampaignsExternalLink Anima" width="324" height="576" />

<a name="Contents"/>

## Contents

This app showcases how to use SessionM SDK to fetch promotional content data for presentation in a content feed.

A content tile can be clicked to see metadata and start video playback (if applicable).

By default it uses the anonymous user. You can click on “Click Here To Login User” to login the default sample user to see the customized contents.

Contents list

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/contents_list.gif" alt="ContentsList Anima" width="324" height="576" />

Content details and play video

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/contents_play.gif" alt="ContentsPlay Anima" width="324" height="576" />

<a name="Geofence"/>

## Geofence

This app showcases how to use SessionM SDK to track the user's location and monitor regions that the user enters and exits.

API Docs: https://mmc.sessionm.com/docs/mmc-sdk/#geofencing

After refresh install the app, you’ll need to follow these steps:
- Grant the location permission request
- Click on my location button to zoom to device’s current location. 
- Click on geofence button on the left-bottom of the map to start the geofence service. 
- Once the geofence events is fetched, the map shows a view of the user's nearby geofence events. You could find different color circles on the map, each circle stands for a geofence region. Each color means a specific type of campaign, like “enter_starbucks”, “exit_dunkin”.
- Move around on the map(we recommend to use any Fake location app to fake device location), once you trigger a geofence event(Enter a “enter_starbucks” region or Exit a “exit_dunkin” region), you could see a local push notification presented in device’s notification center.
- You could also click on “Logs” button at the right-bottom of the map to see all geofence related logs including geofence events list updates, events triggering and more.
- By default, once the geofence service is started, it runs on its service even the app is killed / device is restarted. You need to click on “Geofece” button again to stop the geofence service manually.

By default it uses the anonymous user. You can click on “Click Here To Login User” to login the default sample user.

Geofence Enter Starbucks

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/geofence_enter_starbucks.gif" alt="GeofenceEnter Anima" width="324" height="576" />

Geofence Exit Dunkin

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/geofence_exit_dunkin.gif" alt="GeofenceExit Anima" width="324" height="576" />

<a name="Inbox"/>

## Inbox

This app showcases how to use SessionM SDK to create UI for a user inbox.

API Docs: https://mmc.sessionm.com/docs/mmc-sdk/#inbox

The main view shows a list of messages in the user's inbox, with subjects, creation dates and inbox message body for each message. New message is with blank text color and Read message is with gray text color.

You could left slide on each cell to update message state to “Delete”, “Read” or “Unread”, pull down the list to refresh the inbox messages list.

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/inbox_update_state.gif" alt="InboxUpdateState Anima" width="324" height="576" />

By default it uses the sample user. You can click on “*pts” textview to logout the default sample user and all related inbox message are gone. You could then click on “Click Here To Login User” to login sample user again.

You could also click on the floating button to create a random new inbox message and added to current user’s inbox. You’ll need to pull down to refresh the inbox messages list to see the new message.

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/inbox_create_new.gif" alt="InboxCreate Anima" width="324" height="576" />

<a name="Loyalty_Cards"/>

## Loyalty Cards

This app showcases how to use SessionM SDK to link and unlink loyalty cards to a user's account.

API Docs: https://mmc.sessionm.com/docs/mmc-sdk/#loyalty-card-linking

The main view shows a list of loyalty cards that have been linked to the user's account.

By default it uses the sample user. You can click on “*pts” textview to logout the default sample user and all linked loyalty cards are gone. You could then click on “Click Here To Login User” to login sample user again.

Click on the floating button to link a new card. You can see a list of retailers, you could search them by name, enter your card number and link the card. Then go back to the linked card list you could see the latest linked card. 

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/loyalty_cards_link.gif" alt="LoyaltyCardsLink Anima" width="324" height="576" />

Click on the cell you could unlink the card.

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/loyalty_cards_unlink.gif" alt="LoyaltyCardsUnlink Anima" width="324" height="576" />

<a name="Places"/>

## Places

This app showcases how to use SessionM SDK to allow users to earn points and promotional opportunities by checking in to sponsored venues.

API Docs: https://mmc.sessionm.com/docs/mmc-sdk/#places

After refresh install the app, you’ll need to follow these steps:

- Grant the location permission request
- Click on my location button to zoom to device’s current location. 
- You would see a bunch of venues nearby that are available to check in. 

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/places_fetch_venues.gif" alt="PlacesFetch Anima" width="324" height="576" />

- Click on any of the droppers, you could see venue name and venue ID in the Infobox, click on the infobox, you should be able to make a check in attempt. If the venue is checkable, you’d check in to this venue successfully, otherwise you’ll see a toast describing the error message.

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/places_checkin.gif" alt="PlacesCheckin Anima" width="324" height="576" />

By default it uses the sample user. You can click on “*pts” textview to logout the default sample user and all linked loyalty cards are gone. You could then click on “Click Here To Login User” to login sample user again.

<a name="Push_Notification"/>

## Push Notification

This app showcases how to use SessionM SDK to present a popup message when the user receives a push notification.

API Docs: https://mmc.sessionm.com/docs/mmc-sdk/#push-notifications

There are two push notification sample apps: Firebase Cloud Messaging(FCM) and Google Cloud Messaging(GCM)

They have same UI’s, just uses different service.

By default it uses the sample user. You can click on “*pts” textview to logout the default sample user and all linked loyalty cards are gone. You could then click on “Click Here To Login User” to login sample user again.

There are three different actions to trigger different types of push notification message: Open ad, deep link and external link. Click on any of the buttons triggers a push notification with the specific type. Once you received a push notification in device’s notification center, clicks on it gives you different actions:

- Open Ad: Opens a pre-set Ad in the app’s portal.

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/gcm_open_ad.gif" alt="GCMOpenAd Anima" width="324" height="576" />

- Deep Link: Opens a dialog with a sample deep link schema

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/gcm_deep_link.gif" alt="GCMDeepLink Anima" width="324" height="576" />

- External Link: Opens native browser with sample URL

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/gcm_external_link.gif" alt="GCMExternalLink Anima" width="324" height="576" />

There is also an option on how to handle push notification click actions. For more details, please refer to: https://mmc.sessionm.com/docs/mmc-sdk/#integration

<a name="Receipts"/>

## Receipts

This app showcases how to use SessionM SDK to allow users to upload receipt images.

API Docs: https://mmc.sessionm.com/docs/mmc-sdk/#receipt

The main view shows a list of receipts that have already been uploaded by the user.

By default it uses the sample user. You can click on “*pts” textview to logout the default sample user and all linked loyalty cards are gone. You could then click on “Click Here To Login User” to login sample user again.

You could click on the floating button to upload a new receipt. Follow the steps:

- Grant access external storage permission
- Click on Upload Receipt button
- Pick up an image from either Camera or Files
- You could either “Add more” images, which allows multiple images to be uploaded in one request, or “User Photo” to start uploading.
- You’ll see a status page shows if the image is uploaded successfully. If success, you could see the uploaded receipt showing in the receipts list. Otherwise, the next time you try to upload a new receipt, the app prompts if you want to resume the more recent failed receipt. 

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/receipts_upload.gif" alt="ReceiptsUpload Anima" width="324" height="576" />

Different status receipts

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/receipts_status.gif" alt="ReceiptsStatus Anima" width="324" height="576" />

<a name="Referrals"/>

## Referrals

This app showcases how to use SessionM SDK to allow users to send an email to refer other potential users to sign up for an account.

API Docs: https://mmc.sessionm.com/docs/mmc-sdk/#referrals

The main view shows a list of referrals that have already been sent by the user. 

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/referrals_create_random.gif" alt="ReferralsCreateRandom Anima" width="324" height="576" />

You could click on the floating button to create a new referral. “Create Random” button provides a convenient way to create two random referrals based on timestamp. 
By default it uses the sample user. You can click on “*pts” textview to logout the default sample user and all referrals are gone. You could then click on “Click Here To Login User” to login sample user again.

<a name="Rewards"/>

## Rewards

This app showcases how to use SessionM SDK to allow users to spend loyalty points to purchase rewards.

API Docs: https://mmc.sessionm.com/docs/mmc-sdk/#rewards

The main activity view shows a list of offers that are available for purchase. Click on an offer to see more details and make a purchase. If the offer is gated by a skills test (e.g. for a sweepstakes entry), then an alert will appear with a random question. Enter the correct answer to complete the purchase.

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/rewards_list.gif" alt="RewardsList Anima" width="324" height="576" />

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/rewards_redeem.gif" alt="RewardsRedeem Anima" width="324" height="576" />

The “Orders” button show a list of current user’s history orders.

By default it uses the sample user. You can click on “*pts” textview to logout the default sample user and orders are gone. You could then click on “Click Here To Login User” to login sample user again.

<a name="Transactions"/>

## Transactions

This app showcases how to use SessionM SDK to view a user's loyalty points transaction history.

API Docs: https://mmc.sessionm.com/docs/mmc-sdk/#transactions11

The main view shows a list of loyalty points transactions made by the user, and the resulting points balance after each transaction.

<img src="https://raw.githubusercontent.com/sessionm/android-smp-example/gifs/images/transactions_list.gif" alt="TransactionsList Anima" width="324" height="576" />

By default it uses the sample user. You can click on “*pts” textview to logout the default sample user and all transactions are gone. You could then click on “Click Here To Login User” to login sample user again.
