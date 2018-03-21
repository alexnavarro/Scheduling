# Scheduling

> Permit see professional's availability, make an appointment, change or cancel it.

[Scheduling](https://github.com/alexnavarro/Scheduling)  is an app  made as part of Udacity Android Nanodegree. For now, it shows a list of beauty salons where you can choose one forsee the available professionals and their schedule. To make an appointment the user needs to log in to Google accounts.

It use standard Android architecture and following libs
* [butterknife](https://github.com/JakeWharton/butterknife) for bind Android views
* [Firebase Realtime Data base](https://firebase.google.com/docs/database) for handle data
* [Firebase Auth](https://firebase.google.com/docs/auth) for handle authenticantion
* [Firebase UI](https://github.com/firebase/FirebaseUI-Android) for hande the UI authentication

## Install

I recommend you use [Android Studio](https://developer.android.com/studio/index.html) because the development is easier thant command line. It necessary create a directory named keys in the root and put your debug and production keystore. 

I'm using [Firebase Realtime Data Base](https://firebase.google.com/products/database/) and [Firebase Auth](https://firebase.google.com/docs/auth/) and you need configure it.

For generate the apk using command line use this command: `./gradlew assembleDebug`

## Screenshot
<img src="/screenshots/device-2017-12-10-124952.png" width="280" height="500"> <img src="/screenshots/device-2017-12-10-124617.png" width="280" height="500"> <img src="/screenshots/device-2017-12-10-124603.png" width="280" height="500">

## Meta

Alexandre Navarro – [@navarro_alex](https://twitter.com/navarro_alex) – alexandrenavarro@gmail.com

Distributed under the MIT license. See [LICENSE](LICENSE) for more information.

[https://github.com/alexnavarro](https://github.com/alexnavarro)

## Contributing

1. Fork it (<https://github.com/alexnavarro/Scheduling#fork-destination-box>)
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Commit your changes (`git commit -am 'Add some fooBar'`)
4. Push to the branch (`git push origin feature/fooBar`)
5. Create a new Pull Request

Some other ways you can contribute:
* by suggesting new features
* by writing specifications
* by writing code ( **no patch is too small** : fix typos, add comments, clean up inconsistent whitespace)
* by refactoring code
