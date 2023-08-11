*** Settings ***
Library           Collections
Library           RequestsLibrary
Test Timeout      30 seconds

Suite Setup    Create Session    localhost    http://localhost:8080
#the database needs to be reset and empty for the controlled test environment
#othervise we will get unintended 400 and 500 everywhere
*** Test Cases ***

addActorPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Kavin Bacon   actorId=kavinbacon
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200
# unique id, altho actor exists it has diff name
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Kavin Bacon   actorId=kavinbaconbutuniqueid
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200
# not necessary but sets up some environment ...


addActorFail
#already added person, should fail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Kavin Bacon   actorId=kavinbacon
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400
addActorFail1
    #this needs to fail cuz it has "unique" name but id exists
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=KevinBacon DifferentName   actorId=kavinbacon
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400
addActorFail2
    #I expect this to fail cuz of bad formattiong i wote actorID and NAME wrong
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    Actorid=robertdowney    Name=Robert Downey Jr.
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400
addActorFail3
    #I expect this to fail cuz of nulls
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=${null}   actorId=scarlettjohansson
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400
addActorFail4
    #I expect this to fail cuz of nulls
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Scarlett Johansson   actorId=${null}
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400

addMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A Few Good Men   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200
addMoviePass1
    #pass cus has a diff id
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A Few Good Men   movieId=afewgoodmenuniqueanddifferentid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

     # not necessary but sets up some environment ....
addMovieFail
    #fail cuz movieId exists
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A_Few_Good_Men_butDifferentName   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400
addMovieFail1
    #fail cuz movieId exists
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A_Few_Good_Men_butDifferentName   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400
addMovieFail2
    #fail cuz bad formatting
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=afewgoodmenid   name=A_Few_Good_Men_butDifferentName
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400
addMovieFail3
    #fail cuz movieId exists
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A_Few_Good_Men_butDifferentName   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400
addMovieFail4
    #fail cuz movieId exists
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=A_Few_Good_Men_butDifferentName   movieId=afewgoodmenid
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400
