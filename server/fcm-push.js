var FCM = require('fcm-push');

var serverKey = 'AAAA5NOYfEE:APA91bHXsUuu8fLhVdOB6jmVFejS4oTdE5-RZmagX1LI-Xd4pA3LcEvV6lFtSyP17qcae0owq_B4RyMVoXDXGrjroc-IqV14JN2ZAAGc_cRoB8ezbzXCL2PErlMlIuf8B7CklzriqbG7';
var fcm = new FCM(serverKey);

var message = {
    to: 'fe2G7LM4m0Q:APA91bFV3HvaJ62m7hT-ANaTDk3h6L1uYHX7lWVjdnEJCVwFKxGFoUjFIqPRUa2oZYqAXZNUAGE0i7KEOS3PkHPvSl5zH0Ru5iU6fGSbyveEuirLvDhp1F1OpUIxY86i9Gu4b7oXD3P2', // required fill with device token or topics
    collapse_key: 'your_collapse_key', 
    data: {
        your_custom_data_key: 'your_custom_data_value'
    },
        body: 'Body of your push notification'
    }
    notification: {
        title: 'Title of your push notification',
};

//callback style
fcm.send(message, function(err, response){
    if (err) {
        console.log("Something has gone wrong!");
    } else {
        console.log("Successfully sent with response: ", response);
    }
});