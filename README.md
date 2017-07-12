# PROGRESS
The purpose of this android application is to consolidate news, feeds, and statuses from various social network services into one application. Currently, the following will be retrieved:

  - Facebook news feeds(user_posts, login required)
  - Twitter statuses(home timeline, login required)
  - Google news(Retrieved by News API(https://newsapi.org/), no login required)

(Note that I was intented to get facebook /{user_id}/home instead of /{user_id}/posts, however, the api(/{user_id}/home) has been unavailable since October 6th, 2015, because of user privacy. And /{user_id}/posts can only get posts from the user and post people make in his/her timeline).

# Architecture and Technical Design
The application consists of two activities:

- LoginActivity: This is the main Activity, but just acts like a "place holder", simply enter any email address could proceed to next Activity. The email box will also retrieve user profile to autocomplete user emails.
- <img src="Pics/LogInActivity.png" width="288">

- FeedActivty: Two Fragment will be attached to this Activity, FeedListFragment, which shows a list of Feeds that are sorted in reverse chronological order(i.e latest first), and DetailFragment, which shows detail of the feed when a user clicks the list item showed in FeedListFragment.
- <img src="Pics/FeedActivity_01.png" width="288"> <img src="Pics/FeedActivity_02.png" width="288">
- <img src="Pics/FeedActivity_03.png" width="288"> <img src="Pics/DetailFragment.png" width="288">



Two AsyncTask subclasses are created to download image and send API requests

- DownloadImageTask: which downloads images given url and set ImageView, an ImageCache is created to cache the images.
- SendRequestAsyncTask: which, in parallel, sends requests to and gets responses from Fackbook Graph API, Twitter API and News Feed API(for Google News). An AtomInteger is used to do the synchronization. The feeds from different source will be merged into one and showed in DetailFragment.

# Environment and Requirement
SDK: 25
IDE: Android Studio
OS: Linux 4.4.0-79-generic
Device: Emulator(Nexus 6)

### Todos
 - Configuration Changes Support: A couple of issues will occur when users rotate the device, one of which is that the activity will be destroyed and recreated while AsyncTask still has reference to the old activity. I would like to resolve this by using retained Fragment. 
 - Better User Interface: Mostly forcus on Application Logic at the moment and will make better UI in the future.
 - Add Feeds or Post from more Social Network Service(e.g. LinkedIn).
 - Add support to store previous Feeds on devices.
 - Add Services and Notifications.

