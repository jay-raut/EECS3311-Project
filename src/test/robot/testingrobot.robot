*** Settings ***
Library           Collections
Library           RequestsLibrary
Test Timeout      30 seconds

Suite Setup    Create Session    localhost    http://localhost:8080
*** Test Cases ***
#addActorPass
#    ${headers}=    Create Dictionary    Content-Type=application/json
#    ${params}=    Create Dictionary    name=Danzel Washington    actorId=nm1001213
#    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200
#

addMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Train To Busan   movieId=ttb0123
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

addMovieFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=One More Chance   movieId=ttb0123
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400



#addRelationship
#    ${headers}=    Create Dictionary    Content-Type=application/json
#    ${params}=    Create Dictionary    name=Train To Busan   movieId=ttb0123
#    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200
