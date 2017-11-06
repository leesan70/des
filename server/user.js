var utils = require("./utils")

/**
 * POST 
 * facebook_id
 * facebook_token
 * push_key
 * name
 */
exports.login = function (request, response){
    var query = request.body     
    var connection = utils.getConnection()
	
	connection.connect(function (err){
		if (err){
            console.log(err)
			connection.end()
			return response.json({"code" : "01"})
        }
        
        var selectQuery = "SELECT * FROM `user` WHERE facebook_id = ?"
        connection.query(selectQuery, [query.facebook_id], function(err, result){
            if (err){      
                console.log(err)          
                connection.end()
                return response.json({"code" : "01"})
            }

            if (result.length == 0){
                var data = {
                    'facebook_id' : query.facebook_id,
                    'push_key' : query.push_key,
                    'name' : query.name           
                }
                var insertQuery = "INSERT INTO `user` SET ?;"
                connection.query(insertQuery, data, function(err, result){
                    if (err){
                        console.log(err)
                        console.log(err)
                        connection.end()
                        return response.json({"code" : "01"})
                    }

                    connection.end()
                    return response.json({
                        "code" : "00",
                        "data" : "",
                    })
                })
            } else {
                connection.end()
                return response.json({
                    "code" : "00",
                    "data" : result[0]
                })
            }
        })
    })
}

/**
 * PUT
 * user_id
 * lat
 * lon
 * accuracy
 */
exports.updateLocation = function(request, response){
    var query = request.body
    var connection = utils.getConnection()

    connection.connect(function(err){
        if (err){
            console.log(err)
            connection.end()
            return next(err, null)        
        }

        var geoQuery = "SELECT * FROM buildings WHERE MBRContains(building_polygon, ST_GeomFromText('POINT(" + query.lon + " " + query.lat + ")')) = 1;"
        connection.query(geoQuery, [query.lat, query.lon], function(err, result){
            if (err){
                console.log(err)
                connection.end()
                return next(err, result)
            }

            if (result.length == 0){
                connection.end()
                return response.json({
                    "code" : "00"
                })
            } else {
                var updateQuery = "UPDATE `user` SET ? WHERE user_id = ?;"
                var data = {
                    "current_location" : result[0].building_name
                }
                connection.query(updateQuery, [data, query.user_id], function(err, result){
                    if (err){
                        console.log(err)
                        connection.end()
                        return next(err, result)
                    }
    
                    connection.end()
                    return response.json({
                        "code" : "00"
                    })       
                })
            }  
        })
    })
}

