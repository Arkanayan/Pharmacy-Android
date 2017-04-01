# Pharmacy - Android App

<img src="/images/app_icon.jpg" align="left"
width="200" height="200" hspace="10" vspace="10">

Pharmacy - Android app is the client side app for ordering medicines.  
It is free and open source.  
Pharmacy for Android is a companion app for [Pharmacy Web](https://github.com/Arkanayan/Pharmacy-Web).  
Pharmacy WebApp is for administration of orders on the admin(Pharmacy) side.



## About

With this app, customers can order medicines from the pharmacy it is connected to.
The pharmacy has to host the companion administration web-app [Pharmacy Web](https://github.com/Arkanayan/Pharmacy-Web).


## Features
The android app lets you:
- Authenticate via OTP.
- Super simple sign up process with auto verification of OTP.
- Completely ad-free.
- Material Design.
- Specify medicine names.
- Or take picture of prescription and order.
- Realtime update.
- Update minimum order value in realtime.
- Block specific users from ordering.
- Multiple order statuses.
- Push notification to notifiy order status change.
- Change map of delivery areas in realtime.

## Screenshots
[<img src="/images/new_order.png" align="left"
width="200"
    hspace="10" vspace="10">](/images/new_order.png)
[<img src="/images/order_details.png" align="center"
width="200"
    hspace="10" vspace="10">](/images/order_details.png)
[<img src="/images/shot_order_list.png" align="center"
width="200"
    hspace="10" vspace="10">](/images/shot_order_list.png)

## Permissions

On Android versions prior to Android 6.0, pharmrmacy android requires the following permissions:
- Full Network Access.
- Read and write access to external storage - To access prescription images
- Camera access - To capture image of prescription
- Receive sms - To access OTP code and verify automatically

## Contributing
You are free to use and modify the app.
Just specify appropriate properties in 
```
app/gradle.properties.sample
```
 and rename it to 
 ```
 app/gradle.properties
 ```

## License

This application is released under GNU GPLv3 (see [LICENSE](LICENSE)).
Some of the used libraries are released under different licenses.
