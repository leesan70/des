exports.getConnection = function(){
	var mysql = require("mysql")
	var dbinfo = require("./dbinfo.json")
	var connection = mysql.createConnection(dbinfo)	
	
	return connection
}

