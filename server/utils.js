exports.getConnection = function(){
	var mysql 	= require("mysql")
	var dbinfo 	= require("./config/dbinfo.json")

	dbinfo.multipleStatements = true

	var connection = mysql.createConnection(dbinfo)	
	
	return connection
}

exports.sendFCM = function(data, next){
	var FCM			= require('fcm-node')
	var serverKey 	= require("./config/fcm_key.json")
	var fcm 		= new FCM(serverKey)

	var message = {
		to 				: data.to,
		collapse_key 	: data.collapse_key,
		notification 	: {
			title 	: data.title,
			body 	: data.body
		},
		data 			: {
			my_key	: data.key1,
			my_another_key : data.key2
		}
	}

	fcm.send(message, function(err, response){
		if (err){
			console.log(err)
		} else{
			next()
		}
	})
}