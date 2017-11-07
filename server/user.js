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
 * facebook_id
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
                var data = {
                    "current_location" : ""
                }
            } else {
                var data = {
                    "current_location" : result[0].building_name
                }
            }

            var updateQuery = "UPDATE `user` SET ? WHERE facebook_id = ?;"            
            connection.query(updateQuery, [data, query.facebook_id], function(err, result){
                if (err){
                    console.log(err)
                    connection.end()
                    return response.json({"code" : "01"})
                }

                connection.end()
                return response.json({
                    "code" : "00"
                })       
            })            
        })
    })
}

/**
 * GET
 * facebook_id
 * building_name
 */
exports.getUserInBuilding = function(request, response){
    var query = request.query
    var connection = utils.getConnection()

    connection.connect(function(err){
        if (err){
            console.log(err)
            connection.end()
            return response.json({"code" : "01"})
        }

        var selectQuery = "SELECT * FROM `user` WHERE current_location = ? AND current_location NOT IN (SELECT current_location FROM `user` WHERE facebook_id = ?);"
        connection.query(selectQuery, [query.building_name, query.facebook_id], function(err, result){
            if (err){
                console.log(err)
                connection.end()
                return response.json({"code" : "01"})
            }            

            return response.json({
                "code" : "00",
                "data" : result
            })
        })        
    })
}
/**
 * GET
 * lat
 * lon
 */
exports.getBuildings = function(request, response){
    var query = request.query
    var connection = utils.getConnection()

    connection.connect(function(err){
        if (err){
            console.log(err)
            connection.end()
            return response.json({"code" : "01"})
        }

        var search_radius = 0.001        
        var geoQuery = "SELECT * FROM `buildings` " +
        "WHERE MBRIntersects( ST_BUFFER( POINT( " + query.lon + "," + query.lat + "), " + search_radius +" ), building_polygon );"
        connection.query(geoQuery, function(err, result){
            if (err){
                console.log(err)
                connection.end()
                return response.json({"code" : "01"})
            }

            return response.json(result)
        })
    })
}

/**
 * PUT
 * facebook_id
 * gender
 */
exports.updateGender = function(request, response){
    var query = request.query
    var connection = utils.getConnection()

    connection.connect(function(err){
        if (err){
            console.log(err)
            connection.end()
            return response.json({"code" : "01"})
        }

        var data = {
            'gender' : query.gender
        }
        var updateQuery = "UPDATE `user` SET ? WHERE facebook_id = ?;"
        connection.query(updateQuery,[data, query.facebook_id],function(err, result){
            if (err){
                console.log(err)
                connection.end()
                return response.json({"code" : "01"})
            }

            return response.json({"code" : "00"})            
        })
    })
}

/**
 * PUT
 * facebook_id
 * gender
 */
exports.updatePreference = function(request, response){
    var query = request.query
    var connection = utils.getConnection()

    connection.connect(function(err){
        if (err){
            console.log(err)
            connection.end()
            return response.json({"code" : "01"})
        }

        var data = {
            'gender_pref' : query.gender
        }
        var updateQuery = "UPDATE `user` SET ? WHERE facebook_id = ?;"
        connection.query(updateQuery,[data, query.facebook_id],function(err, result){
            if (err){
                console.log(err)
                connection.end()
                return response.json({"code" : "01"})
            }

            return response.json({"code" : "00"})            
        })
    })
}