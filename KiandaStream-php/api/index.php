<?php

@session_start();

@ob_start();

@ob_implicit_flush(0);

@error_reporting(E_ALL ^ E_NOTICE);

@ini_set('display_errors', true);

@ini_set('html_errors', false);

@ini_set('error_reporting', E_ALL ^ E_NOTICE);

define('ROOT_DIR', dirname(__FILE__));

define('INCLUDE_DIR', ROOT_DIR . '/includes');

include (INCLUDE_DIR . '/config.inc.php');

require_once INCLUDE_DIR . '/class/_class_mysql.php';

require_once INCLUDE_DIR . '/db.php';

require_once INCLUDE_DIR . '/member.php';

require_once ROOT_DIR . '/modules/functions.php';

require_once './mandrill-api-php/src/Mandrill.php';

header('Content-type: text/json');

header('Content-type: application/json');
$logged = 1;

$_TIME = date("Y-m-d H:i:s", time());
if (isset($_REQUEST ['t']))
    $type = $_REQUEST ['t'];

if (isset($_REQUEST ['start']))
    $start = intval($_REQUEST ['start']);

if ($type == "createAccount") {
//echo "test";die;
    $username = $db->safesql($_REQUEST['username']);
    $password = $_REQUEST['password'];
    $email = $db->safesql($_REQUEST['email']);
//    $bandname = $db->safesql($_POST['bandname']);
//    $bio = $db->safesql($_POST['bio']);
//    $user_id = $db->safesql($_POST['user_id']);

    $REGISTER = TRUE;

    if ($email && $username && $password) {


        if (!preg_match("/^([a-zA-Z0-9])+([a-zA-Z0-9\._-])*@([a-zA-Z0-9_-])+([a-zA-Z0-9\._-]+)+$/", $email)) {

            $buffer['status_code'] = 205;
            $buffer['status_text'] = "Email is not valid";
            $REGISTER = FALSE;
        } elseif (preg_match('/[^-a-z0-9_.-]/i', $username)) {

            $buffer['status_code'] = 400;
            $buffer['status_text'] = "Username must be not contain special word, or symbol";

            $REGISTER = FALSE;
        } elseif (strlen($password) < 6) {

            $buffer['status_code'] = 203;
            $buffer['status_text'] = "Password is too short!";

            $REGISTER = FALSE;
        } else {

            $row = $db->super_query("SELECT user_id FROM vass_users WHERE email = '" . $email . "' LIMIT 0,1");

            if ($row['user_id']) {

                $buffer['status_code'] = 201;
                $buffer['status_text'] = "That email address is already in use.";

                $REGISTER = FALSE;
            } else {

                $row = $db->super_query("SELECT user_id FROM vass_users WHERE username = '" . $username . "' LIMIT 0,1");

                if ($row['user_id']) {

                    $buffer['status_code'] = 202;

                    $buffer['status_text'] = "That username address is already in use.";

                    $REGISTER = FALSE;
                }
            }
        }
    } else {

        $buffer['status_code'] = 206;
        $buffer['status_text'] = "Please enter username, email and bandname.";
        $REGISTER = FALSE;
    }



    if ($REGISTER) {

        $db->query("INSERT INTO vass_users SET username = '" . $username . "', email = '" . $email . "',  password = '" . md5($password) . "', reg_date = '$_TIME'");

        $user_id = $db->insert_id();
        $row = $db->super_query("SELECT * FROM vass_users WHERE user_id = '" . $user_id . "'");
        $buffer['status_code'] = 200;
        $buffer['status_text'] = "Success.";
        $buffer['user'] = $row;

        $token = md5(rand(100000, 900000));
        $buffer['user']['access_token'] = $token;
        unset($buffer ['user'] ['password']);
        unset($buffer ['user'] ['email']);
        $db->query("INSERT INTO vass_session SET user_id='" . $user_id . "', token = '" . $token . "'");
    }
    print json_encode($buffer);
} elseif ($type == "validation") {

    $username = $db->safesql($_REQUEST['username']);
//    $email = $db->safesql($_REQUEST['email']);
//    $password = $db->safesql($_REQUEST['password']);
    $REGISTER = TRUE;
//    echo $username;die;
    if ($email) {


        if (preg_match('/[^-a-z0-9_.-]/i', $username)) {

            $buffer['status_code'] = 202;
            $buffer['status_text'] = "Username must be not contain special word, or symbol";
            $buffer['register'] = "invalid";
            $REGISTER = FALSE;
        } else {



            $row = $db->super_query("SELECT user_id FROM vass_users WHERE username = '" . $username . "' LIMIT 0,1");

            if ($row['user_id']) {

                $buffer['status_code'] = 202;

                $buffer['status_text'] = "That username address is already in use.";
                $buffer['register'] = "invalid";
                $REGISTER = FALSE;
            }
        }
    } else {

        $buffer['status_code'] = 202;
        $buffer['status_text'] = "Please enter username";
        $buffer['register'] = "invalid";
        $REGISTER = FALSE;
    }

    if ($REGISTER) {
        $buffer['status_code'] = 206;
        $buffer['register'] = "valid";
    }


    print json_encode($buffer);
} elseif ($type == "facebooklogin") {


    $screen_id = $db->safesql($_REQUEST['fb_id']);
//    $token = $db->safesql($_REQUEST['token']);
//    $img_url = $db->safesql($_REQUEST['profile_image_url']);

    if ($screen_id) {

        $row = $db->super_query("SELECT user_id FROM vass_facebook WHERE screen_id = '" . $screen_id . "' LIMIT 0,1");

        if ($row['user_id']) {

            $row1 = $db->super_query("SELECT * FROM vass_users WHERE user_id = '" . $row['user_id'] . "'");


            $buffer['status_code'] = 200;
            $buffer['status_text'] = "Success";
            $buffer['user'] = $row1;

            $token = md5(rand(100000, 900000));
            $db->query("INSERT INTO vass_session SET user_id='" . $row['user_id'] . "', token = '" . $token . "', created_on = '$_TIME' ON DUPLICATE KEY UPDATE user_id='" . $row['user_id'] . "', token = '" . $token . "', created_on = '$_TIME';");
            $buffer['user']['access_token'] = $token;
            unset($buffer ['user'] ['password']);
            unset($buffer ['user'] ['email']);
        } else {

            $buffer['status_code'] = 201;
            $buffer['status_text'] = "Do first facebook signup";
        }
    } else {

        $buffer['status_code'] = 202;
        $buffer['status_text'] = "Please send name, Fb_id ";
    }

    print json_encode($buffer);
} elseif ($type == "facebooksignup") {

    $username = $db->safesql($_REQUEST['username']);
    $email = $db->safesql($_REQUEST['email']);
    $password = $db->safesql($_REQUEST['password']);
    $name = $db->safesql($_REQUEST['name']);
    $screen_id = $db->safesql($_REQUEST['fb_id']);
//    $token = $db->safesql($_REQUEST['token']);

    $REGISTER = TRUE;

    if ($email && $username && $password) {


        if (!preg_match("/^([a-zA-Z0-9])+([a-zA-Z0-9\._-])*@([a-zA-Z0-9_-])+([a-zA-Z0-9\._-]+)+$/", $email)) {

            $buffer['status_code'] = 205;
            $buffer['status_text'] = "Email is not valid";
            $REGISTER = FALSE;
        } elseif (preg_match('/[^-a-z0-9_.-]/i', $username)) {

            $buffer['status_code'] = 400;
            $buffer['status_text'] = "Username must be not contain special word, or symbol";

            $REGISTER = FALSE;
        } elseif (strlen($password) < 6) {

            $buffer['status_code'] = 203;
            $buffer['status_text'] = "Password is too short!";

            $REGISTER = FALSE;
        } else {

            $row = $db->super_query("SELECT user_id FROM vass_users WHERE email = '" . $email . "' LIMIT 0,1");

            if ($row['user_id']) {

                $buffer['status_code'] = 201;
                $buffer['status_text'] = "That email address is already in use.";

                $REGISTER = FALSE;
            } else {

                $row = $db->super_query("SELECT user_id FROM vass_users WHERE username = '" . $username . "' LIMIT 0,1");

                if ($row['user_id']) {

                    $buffer['status_code'] = 202;

                    $buffer['status_text'] = "That username address is already in use.";

                    $REGISTER = FALSE;
                }
            }
        }
    } else {

        $buffer['status_code'] = 206;
        $buffer['status_text'] = "Please enter username, email and password.";
        $REGISTER = FALSE;
    }



    if ($REGISTER) {

        $db->query("INSERT INTO vass_users SET username = '" . $username . "', email = '" . $email . "',  password = '" . md5($password) . "', reg_date = '$_TIME'");

        $user_id = $db->insert_id();
        $row = $db->super_query("SELECT * FROM vass_users WHERE user_id = '" . $user_id . "'");

        $buffer['status_code'] = 200;
        $buffer['status_text'] = "Success";
        $buffer['user'] = $row;

        $token = md5(rand(100000, 900000));
        $buffer['user']['access_token'] = $token;


        $db->query("INSERT INTO vass_facebook SET user_id = '" . $user_id . "', screen_id = '" . $screen_id . "', screen_name = '" . $username . "', name = '" . $name . "', profile_image_url = 'https://graph.facebook.com/" . $screen_id . "/picture?type=large', date = '" . date("Y-m-d H:i:s", time()) . "'");
        $db->query("INSERT INTO vass_session SET user_id='" . $user_id . "', token = '" . $token . "'");
        unset($buffer ['user'] ['password']);
        unset($buffer ['user'] ['email']);
    }
    print json_encode($buffer);
} elseif ($type == "twitterlogin") {


    $screen_id = $db->safesql($_REQUEST['twt_id']);
//    $token = $db->safesql($_REQUEST['token']);
//    $img_url = $db->safesql($_REQUEST['profile_image_url']);

    if ($screen_id) {

        $row = $db->super_query("SELECT user_id FROM vass_twitter WHERE screen_id = '" . $screen_id . "' LIMIT 0,1");

        if ($row['user_id']) {

            $row1 = $db->super_query("SELECT * FROM vass_users WHERE user_id = '" . $row['user_id'] . "'");

            $buffer['status_code'] = 200;
            $buffer['status_text'] = "Success";
            $buffer['user'] = $row1;

            $token = md5(rand(100000, 900000));
            $db->query("INSERT INTO vass_session SET user_id='" . $row['user_id'] . "', created_on = '$_TIME' ON DUPLICATE KEY UPDATE user_id='" . $row['user_id'] . "', token = '" . $token . "', created_on = '$_TIME';");

            unset($buffer ['user'] ['password']);
            unset($buffer ['user'] ['email']);
            $buffer['user']['access_token'] = $token;
        } else {

            $buffer['status_code'] = 201;
            $buffer['status_text'] = "Do first twitter signup";
        }
    } else {

        $buffer['status_code'] = 202;
        $buffer['status_text'] = "Please send name, twt_id ";
    }

    print json_encode($buffer);
} elseif ($type == "twittersignup") {

    $username = $db->safesql($_REQUEST['username']);
    $email = $db->safesql($_REQUEST['email']);
    $password = $db->safesql($_REQUEST['password']);
    $name = $db->safesql($_REQUEST['name']);
    $screen_id = $db->safesql($_REQUEST['twt_id']);
    $img_url = $db->safesql($_REQUEST['profile_image_url']);
    $location = $db->safesql($_REQUEST['location']);
    $description = $db->safesql($_REQUEST['description']);

//    $token = $db->safesql($_REQUEST['token']);
//echo"<pre>";print_r($username);echo"<pre>";die;
    $REGISTER = TRUE;

    if ($email && $username && $password) {


        if (!preg_match("/^([a-zA-Z0-9])+([a-zA-Z0-9\._-])*@([a-zA-Z0-9_-])+([a-zA-Z0-9\._-]+)+$/", $email)) {

            $buffer['status_code'] = 205;
            $buffer['status_text'] = "Email is not valid";
            $REGISTER = FALSE;
        } elseif (preg_match('/[^-a-z0-9_.-]/i', $username)) {

            $buffer['status_code'] = 400;
            $buffer['status_text'] = "Username must be not contain special word, or symbol";

            $REGISTER = FALSE;
        } elseif (strlen($password) < 6) {

            $buffer['status_code'] = 203;
            $buffer['status_text'] = "Password is too short!";

            $REGISTER = FALSE;
        } else {

            $row = $db->super_query("SELECT user_id FROM vass_users WHERE email = '" . $email . "' LIMIT 0,1");

            if ($row['user_id']) {

                $buffer['status_code'] = 201;
                $buffer['status_text'] = "That email address is already in use.";

                $REGISTER = FALSE;
            } else {

                $row = $db->super_query("SELECT user_id FROM vass_users WHERE username = '" . $username . "' LIMIT 0,1");

                if ($row['user_id']) {

                    $buffer['status_code'] = 202;

                    $buffer['status_text'] = $row['user_id'];

                    $REGISTER = FALSE;
                }
            }
        }
    } else {

        $buffer['status_code'] = 206;
        $buffer['status_text'] = "Please enter username, email and password.";
        $REGISTER = FALSE;
    }



    if ($REGISTER) {

        $db->query("INSERT INTO vass_users SET username = '" . $username . "', email = '" . $email . "', password = '" . md5($password) . "', reg_date = '" . date("Y-m-d H:i:s", time()) . "', name = '" . $name . "', location = '" . $location . "', bio  = '" . $description . "', avatar = '1'");

        $user_id = $db->insert_id();
        $row = $db->super_query("SELECT * FROM vass_users WHERE user_id = '" . $user_id . "'");

        $buffer['status_code'] = 200;
        $buffer['status_text'] = "Success";
        $buffer['user'] = $row;

        $token = md5(rand(100000, 900000));
        $buffer['user']['access_token'] = $token;


        $db->query("INSERT IGNORE INTO vass_twitter SET user_id = '" . $user_id . "', screen_id = '" . $screen_id . "',
				screen_name = '" . $username . "', name = '" . name . "',
				profile_image_url = '" . $img_url . "', `date` = '$_TIME'
				");
        $db->query("INSERT INTO vass_session SET user_id='" . $user_id . "', token = '" . $token . "'");
        unset($buffer ['user'] ['password']);
        unset($buffer ['user'] ['email']);
    }

    print json_encode($buffer);
} elseif ($type == "getSignonToken") {

    $username = $db->safesql($_REQUEST['username']);
    $password = $db->safesql(md5($_REQUEST['password']));

    $row = $db->super_query("SELECT * FROM vass_users WHERE username = '" . $username . "' AND password = '" . $password . "'");

    if (!$row['user_id']) {
        $buffer['status_code'] = 201;
        $buffer['status_text'] = "Wrong username or password";
    } else {



        $buffer['status_code'] = 200;
        $buffer['status_text'] = "Success";
        $buffer['user'] = $row;



        $token = md5(rand(100000, 900000));
        $db->query("INSERT INTO vass_session SET user_id='" . $row['user_id'] . "', token = '" . $token . "', created_on = '$_TIME' ON DUPLICATE KEY UPDATE user_id='" . $row['user_id'] . "', token = '" . $token . "', created_on = '$_TIME';");
        $buffer['user']['access_token'] = $token;
        unset($buffer ['user'] ['password']);
        unset($buffer ['user'] ['email']);
    }

    print json_encode($buffer);
} elseif ($type == "getAccountInfo") {

    $artist_id = intval($_POST['artist_id']);

    $user_id = $db->super_query("SELECT user_id FROM vass_artists WHERE id = '" . $artist_id . "'");
    $user_id = $user_id['user_id'];
    $row = $db->super_query("SELECT * FROM vass_users WHERE user_id = '" . $user_id . "'");

    if (!$row['user_id']) {
        $buffer['status_code'] = 201;
        $buffer['status_text'] = "User not found!";
    } else {

        $artist = $db->super_query("SELECT * FROM vass_artists WHERE user_id = '" . $user_id . "'");

        $buffer['status_code'] = 200;
        $buffer['status_text'] = "Success";
        $buffer['user']['user_id'] = $row['user_id'];
        $buffer['user']['email'] = $row['email'];
        $buffer['user']['username'] = $row['username'];

        $buffer['user']['artist_id'] = $artist['id'];
        $buffer['user']['artist_name'] = $artist['name'];
        $buffer['user']['artist_bio'] = $artist['bio'];

        $token = md5(rand(100000, 900000));
        $db->query("INSERT INTO vass_session SET user_id='" . $row['user_id'] . "', token = '" . $token . "', created_on = '$_TIME' ON DUPLICATE KEY UPDATE user_id='" . $row['user_id'] . "', token = '" . $token . "', created_on = '$_TIME';");
        $buffer['user']['access_token'] = $token;
    }
} elseif ($type == "updateAccount") {

    $user_id = intval($_REQUEST['user_id']);
    $token = $_REQUEST['access_token'];
//    echo $user_id;

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");
//    print_r( $row);die;
    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        if ($_REQUEST['password']) {
            $password = md5($_REQUEST['password']);

            $db->query("UPDATE vass_users SET password = '$password' WHERE user_id = '" . $user_id . "'");

            $buffer['status_code'] = 200;
            $buffer['status_text'] = "Success!Password updated....";
        } else if ($_REQUEST['email']) {
            $email = $db->safesql($_REQUEST['email']);

            $db->query("UPDATE vass_users SET email = '$email', bio = '$bio' WHERE user_id = '" . $user_id . "'");

            $buffer['status_code'] = 200;
            $buffer['status_text'] = "Success!email updated....";
        } elseif ($_REQUEST['password'] && $_REQUEST['email']) {
            $password = md5($_REQUEST['password']);
            $email = ($_REQUEST['email']);

            $db->query("UPDATE vass_users SET email = '$email', bio = '$bio' WHERE user_id = '" . $user_id . "'");
            $db->query("UPDATE vass_users SET email = '$email', bio = '$bio' WHERE user_id = '" . $user_id . "'");

            $buffer['status_code'] = 200;
            $buffer['status_text'] = "Success!password and email updated...";
        }
    } else {
        $buffer['status_code'] = 201;
        $buffer['status_text'] = "Access denied.....";
    }
    print json_encode($buffer);
} elseif ($type == "getArtistAllSongs") {

    $user_id = intval($_REQUEST['user_id']);
    $artist_id = intval($_REQUEST['artist_id']);
    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $sql_result = $db->query("SELECT vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
	vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id FROM vass_songs LEFT JOIN vass_albums ON 
	vass_songs.album_id = vass_albums.id LEFT JOIN vass_artists ON vass_songs.artist_id = vass_artists.id 
	WHERE vass_songs.artist_id = '$artist_id'");


        $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_songs WHERE artist_id = '$artist_id'");
        ;

        while ($row = $db->get_row($sql_result)) {

            $songs ['album'] = $row ['song_album'];
            $songs ['artist'] = $row ['song_artist'];
//            $songs ['url'] = "http://api.kiandastream.com/stream.php?id=" . ( $row ['song_id'] );
            $songs ['title'] = stripslashes($row ['song_title']);
            $songs ['id'] = $row ['song_id'];
            $buffer ['songs'] [] = $songs;
        }

        $buffer ['status_text'] = "success";
        $buffer ['status_code'] = 200;
        $buffer ['total'] = intval($total_results['count']);
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }

    print json_encode($buffer);
} elseif ($type == "getContestSongURL") {
//    echo "test";die;

    $user_id = intval($_REQUEST['user_id']);
    $song_id = intval($_REQUEST['song_id']);
    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {


        $row = $db->super_query("SELECT vass_songs.song_country, vass_songs.tags, vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
	vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id FROM vass_songs LEFT JOIN vass_albums ON 
	vass_songs.album_id = vass_albums.id LEFT JOIN vass_artists ON vass_songs.artist_id = vass_artists.id 
	WHERE vass_songs.id = '$song_id'");


        if ($row ['song_id']) {
            $songs ['album'] = $row ['song_album'];
            $songs ['artist'] = $row ['song_artist'];
            $songs ['genre'] = $row ['tags'];
            $songs ['location'] = $row ['location'];
//            $songs ['url'] = "http://api.kiandastream.com/stream.php?id=" . ( $row ['song_id'] );
            $songs ['title'] = stripslashes($row ['song_title']);
            $songs ['id'] = $row ['song_id'];
            $buffer ['song'] = $songs;

            $buffer ['status_text'] = "OK";
            $buffer ['status_code'] = 200;
        } else {
            $buffer ['status_text'] = "Song not found";
            $buffer ['status_code'] = 201;
        }
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    print json_encode($buffer);
} elseif ($type == "getDeleteSong") {

    $song_id = intval($_REQUEST['song_id']);
    $artist_id = intval($_REQUEST['artist_id']);

    $user_id = $db->super_query("SELECT user_id FROM vass_artists WHERE id = '" . $artist_id . "'");

    $user_id = $user_id['user_id'];

    $row = $db->super_query("SELECT id AS song_id FROM vass_songs WHERE id = '$song_id' and user_id='$user_id'");

    if ($row ['song_id']) {
        $db->query("DELETE FROM vass_songs WHERE id = '$song_id' and user_id='$user_id'");
        $buffer ['status_text'] = "OK";
        $buffer ['status_code'] = "200";
    } else {
        $buffer ['status_text'] = "Song not found";
        $buffer ['status_code'] = "201";
    }
} elseif ($type == "getUploadSong") {

    $artist_id = intval($_REQUEST['artist_id']);

    $user_id = $db->super_query("SELECT user_id FROM vass_artists WHERE id = '" . $artist_id . "'");
    $user_id = $user_id['user_id'];

    $access_token = $db->safesql($_REQUEST['access_token']);
    $song_genre = $db->safesql($_REQUEST['song_genre']);
    $song_location = $db->safesql($_REQUEST['song_location']);
    $contest_id = $db->safesql($_REQUEST['contest_id']);

    $title = $db->safesql($_REQUEST['song_title']);

    $file = $db->safesql($_POST[file]);
    $file_cover_image = $db->safesql($_POST[file_cover_image]);

    if ($title && $file) {

        $db->query("INSERT INTO  vass_songs (contest_id, song_temp, cover_temp, s3, tags, location, title, artist_id, user_id, active, created_on) VALUES ('$contest_id', '$file', '$file_cover_image', '$contest_id', '$song_genre', '$song_location', '$title', '$artist_id', '" . $user_id . "', '1', '$_TIME')");

        $song_id = $db->insert_id();





        $row = $db->super_query("SELECT vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
	vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id FROM vass_songs LEFT JOIN vass_albums ON 
	vass_songs.album_id = vass_albums.id LEFT JOIN vass_artists ON vass_songs.artist_id = vass_artists.id 
	WHERE vass_songs.id = '$song_id'");

        $songs ['album'] = $row ['song_album'];
        $songs ['artist'] = $row ['song_artist'];
//        $songs ['url'] = "http://api.kiandastream.com/stream.php?id=" . ( $row ['song_id'] );
        $songs ['title'] = stripslashes($row ['song_title']);
        $songs ['id'] = $row ['song_id'];
        $buffer ['song'] = $songs;


        $buffer ['status_text'] = "OK";
        $buffer ['status_code'] = "200";
        //added by Ben
        $buffer ['song_id'] = $row ['song_id'];
    }
} elseif ($type == "getUpdateSong") {

    $artist_id = intval($_REQUEST['artist_id']);
    $song_id = intval($_REQUEST['song_id']);

    $user_id = $db->super_query("SELECT user_id FROM vass_artists WHERE id = '" . $artist_id . "'");
    $user_id = $user_id['user_id'];

    $access_token = $db->safesql($_REQUEST['access_token']);
    $song_genre = $db->safesql($_REQUEST['song_genre']);
    $song_location = $db->safesql($_REQUEST['song_location']);
    $contest_id = $db->safesql($_REQUEST['contest_id']);

    $title = $db->safesql($_REQUEST['song_title']);

    $file_cover_image = $db->safesql(trim($_POST['file_cover_image']));
    $file = $db->safesql($_POST[file]);

    if ($title) {

        $db->query("UPDATE vass_songs SET contest_id='$contest_id', tags = '$song_genre', location = '$song_location', title = '$title' WHERE id = '$song_id'");

        //Added because contest guy send suck thing
        $no_change = substr($file_cover_image, 0, 4);


        if ($no_change != 'aHR0' && $file_cover_image) {
            $db->query("UPDATE vass_songs SET cover_temp='$file_cover_image' WHERE id = '$song_id'");
            Exec("rm -f /home/liveinyo/public_html/static/songs/covers/" . $song_id . "_250.jpg");
            Exec("rm -f /home/liveinyo/public_html/static/songs/covers/" . $song_id . "_57.jpg");
            $db->query("INSERT INTO vass_reports SET browser='$song_id', content = '$file_cover_image'");
        }
        if ($_REQUEST['removecover'])
            $db->query("UPDATE vass_songs SET cover='0', cover_temp = '' WHERE id = '$song_id'");

        if ($file)
            $db->query("UPDATE vass_songs SET song_temp='$file', s3 = '0' WHERE id = '$song_id'");




        $row = $db->super_query("SELECT vass_songs.location, vass_songs.tags, vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
	vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id FROM vass_songs LEFT JOIN vass_albums ON 
	vass_songs.album_id = vass_albums.id LEFT JOIN vass_artists ON vass_songs.artist_id = vass_artists.id 
	WHERE vass_songs.id = '$song_id'");

        $songs ['album'] = $row ['song_album'];
        $songs ['artist'] = $row ['song_artist'];
//        $songs ['url'] = "http://api.kiandastream.com/stream.php?id=" . ( $row ['song_id'] );
        $songs ['title'] = stripslashes($row ['song_title']);
        $songs ['id'] = $row ['song_id'];
        $songs ['genre'] = $row ['tags'];
        $songs ['location'] = $row ['location'];
        $buffer ['song_id'] = $row ['song_id'];


        $buffer ['song'] = $songs;


        $buffer ['status_text'] = "OK";
        $buffer ['status_code'] = "200";
    }
}elseif ($type == "getSendWellcome") {

    $fullname = $db->safesql(trim($_REQUEST['fullname']));
    $username = $db->safesql(trim($_REQUEST['username']));
    $email = $db->safesql(trim($_REQUEST['email']));

    $row = $db->super_query("SELECT * FROM vass_email WHERE name = 'contest'");

    $row['template'] = stripslashes(str_replace("{%username%}", $username, $row['template']));
    $row['template'] = stripslashes(str_replace("{%fullname%}", $fullname, $row['template']));


    $message = <<<HTML
<html><title>{$row['subject']}</title>
<meta content="text/html; charset=utf-8" http-equiv=Content-Type>
<style type="text/css">
html,body{
font-size: 11px;
font-family: verdana;
}

a:active,
a:visited,
a:link {
	color: #4b719e;
	text-decoration:none;
	}

a:hover {
	color: #4b719e;
	text-decoration: underline;
	}
</style>
<body>
{$row['template']}
</body>
</html>
HTML;

    require_once INCLUDE_DIR . '/class/_class_mail.php';
    $mail = new class_mail($config, true);
    $mail->send($email, $row['subject'], $message);

    $buffer ['status_text'] = "OK";
    $buffer ['status_code'] = "200";
} elseif ($type == "top") {

//    $top_date = trim($_REQUEST ['date']);
    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

//        if ($top_date == "the-day")
//            $D_SORT = 1;
//        elseif ($top_date == "the-week")
//            $D_SORT = 7;
//        elseif ($top_date == "the-month")
//            $D_SORT = 30;
//        elseif ($top_date == "the-year")
//            $D_SORT = 356;
//        elseif ($top_date == "all-time")
//            $D_SORT = 800;
//
//        $page = intval($_REQUEST['page']);
//        echo gettype($page);die;
//        if (!empty($page) && $page != 0) {
        // the-day

        $D_SORT = 1;

        $total_results = $db->super_query("SELECT COUNT(*) AS count FROM (SELECT COUNT(*) AS bit, song_id FROM vass_analz WHERE `time` > '" . date("Y-m-d", (time() - $D_SORT * 24 * 3600)) . "' GROUP BY song_id ORDER by bit DESC LIMIT 30) AS count");
//                print_r( $total_results);
//                die;
//            $limit = 20;
//            $newpage = $page * $limit;
////                print_r( $total_results);
////                die;
////            if ($page > 1 && $newpage <= $total_results['count']) {
//////                echo "test";
//////                die;
////                $previous = $page - 1;
////            }
////            if ($newpage < $total_results['count']) {
////                $next = $page + 1;
////            }
////            if ($page > 1) {
////                $page = ($page - 1) * $limit;
////            } else {
////                $page = 0;
////            }


        $top_week = $db->query("SELECT COUNT(*) AS count, song_id FROM vass_analz WHERE `time` > '" . date("Y-m-d", (time() - $D_SORT * 24 * 3600)) . "' GROUP BY song_id ORDER by count DESC LIMIT 30 ");

//                              print_r( $top_week);
//                die;  
        while ($top = $db->get_row($top_week)) {

            if ($top ['song_id']) {
                $row = $db->super_query("SELECT vass_songs.artist_id, vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
			vass_artists.id AS artist_id, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id 
			FROM vass_songs LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN 
			vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_songs.id = '" . $top ['song_id'] . "'");
                $songs ['album'] = $row ['song_album'];
                $songs ['artist_id'] = $row ['artist_id'];
                $songs ['similar_artists'] = similar_artists($row ['song_id']);
                $songs ['buy_link'] = null;
                $songs ['artist'] = $row ['song_artist'];
//                    $songs ['url'] = stream($row ['song_id']);
                $songs ['image'] = songlist_images($row ['album_id']);
                $songs ['title'] = $row ['song_title'];
                $songs ['metadata_state'] = metadata_state($row ['song_id']);
                $songs ['sources'] = sources($row ['song_id']);
                $songs ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                $songs ['last_loved'] = null;
                $songs ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                $songs ['aliases'] = aliases($row ['song_id']);
                $songs ['loved_count'] = $row ['loved'];
                $songs ['id'] = $row ['song_id'];
                $songs ['tags'] = tags($row ['song_id']);
                $songs ['trending_rank_today'] = trending_rank_today($row ['song_id']);
                $songs ['user_love'] = "";
                $result ['day-songs'] [] = $songs;
            }
        }

        if ($songs == null) {
            $result ['day-songs'] = array();
        }
        $result ['day-total'] = $total_results['count'];



        $D_SORT = 7;

        $total_results = $db->super_query("SELECT COUNT(*) AS count FROM (SELECT COUNT(*) AS bit, song_id FROM vass_analz WHERE `time` > '" . date("Y-m-d", (time() - $D_SORT * 24 * 3600)) . "' GROUP BY song_id ORDER by bit DESC LIMIT 10) AS count");
        $top_week = $db->query("SELECT COUNT(*) AS count, song_id FROM vass_analz WHERE `time` > '" . date("Y-m-d", (time() - $D_SORT * 24 * 3600)) . "' GROUP BY song_id ORDER by count DESC LIMIT 10 ");

//                              print_r( $top_week);
//                die;  
        while ($top = $db->get_row($top_week)) {

            if ($top ['song_id']) {
                $row = $db->super_query("SELECT vass_songs.artist_id, vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
			vass_artists.id AS artist_id, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id 
			FROM vass_songs LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN 
			vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_songs.id = '" . $top ['song_id'] . "'");
                $songs ['album'] = $row ['song_album'];
                $songs ['artist_id'] = $row ['artist_id'];
                $songs ['similar_artists'] = similar_artists($row ['song_id']);
                $songs ['buy_link'] = null;
                $songs ['artist'] = $row ['song_artist'];
//                    $songs ['url'] = stream($row ['song_id']);
                $songs ['image'] = songlist_images($row ['album_id']);
                $songs ['title'] = $row ['song_title'];
                $songs ['metadata_state'] = metadata_state($row ['song_id']);
                $songs ['sources'] = sources($row ['song_id']);
                $songs ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                $songs ['last_loved'] = null;
                $songs ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                $songs ['aliases'] = aliases($row ['song_id']);
                $songs ['loved_count'] = $row ['loved'];
                $songs ['id'] = $row ['song_id'];
                $songs ['tags'] = tags($row ['song_id']);
                $songs ['trending_rank_today'] = trending_rank_today($row ['song_id']);
                $songs ['user_love'] = "";
                $result ['week-songs'] [] = $songs;
            }
        }

        if ($songs == null) {
            $result ['week-songs'] = array();
        }
        $result ['week-total'] = $total_results['count'];



        $D_SORT = 30;

        $total_results = $db->super_query("SELECT COUNT(*) AS count FROM (SELECT COUNT(*) AS bit, song_id FROM vass_analz WHERE `time` > '" . date("Y-m-d", (time() - $D_SORT * 24 * 3600)) . "' GROUP BY song_id ORDER by bit DESC LIMIT 20) AS count");
        $top_week = $db->query("SELECT COUNT(*) AS count, song_id FROM vass_analz WHERE `time` > '" . date("Y-m-d", (time() - $D_SORT * 24 * 3600)) . "' GROUP BY song_id ORDER by count DESC LIMIT 20 ");

//                              print_r( $top_week);
//                die;  
        while ($top = $db->get_row($top_week)) {

            if ($top ['song_id']) {
                $row = $db->super_query("SELECT vass_songs.artist_id, vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
			vass_artists.id AS artist_id, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id 
			FROM vass_songs LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN 
			vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_songs.id = '" . $top ['song_id'] . "'");
                $songs ['album'] = $row ['song_album'];
                $songs ['artist_id'] = $row ['artist_id'];
                $songs ['similar_artists'] = similar_artists($row ['song_id']);
                $songs ['buy_link'] = null;
                $songs ['artist'] = $row ['song_artist'];
//                    $songs ['url'] = stream($row ['song_id']);
                $songs ['image'] = songlist_images($row ['album_id']);
                $songs ['title'] = $row ['song_title'];
                $songs ['metadata_state'] = metadata_state($row ['song_id']);
                $songs ['sources'] = sources($row ['song_id']);
                $songs ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                $songs ['last_loved'] = null;
                $songs ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                $songs ['aliases'] = aliases($row ['song_id']);
                $songs ['loved_count'] = $row ['loved'];
                $songs ['id'] = $row ['song_id'];
                $songs ['tags'] = tags($row ['song_id']);
                $songs ['trending_rank_today'] = trending_rank_today($row ['song_id']);
                $songs ['user_love'] = "";
                $result ['month-songs'] [] = $songs;
            }
        }

        if ($songs == null) {
            $result ['month-songs'] = array();
        }
        $result ['month-total'] = $total_results['count'];


        $D_SORT = 356;

        $total_results = $db->super_query("SELECT COUNT(*) AS count FROM (SELECT COUNT(*) AS bit, song_id FROM vass_analz WHERE `time` > '" . date("Y-m-d", (time() - $D_SORT * 24 * 3600)) . "' GROUP BY song_id ORDER by bit DESC LIMIT 30) AS count");
        $top_week = $db->query("SELECT COUNT(*) AS count, song_id FROM vass_analz WHERE `time` > '" . date("Y-m-d", (time() - $D_SORT * 24 * 3600)) . "' GROUP BY song_id ORDER by count DESC LIMIT 30 ");

//                              print_r( $top_week);
//                die;  
        while ($top = $db->get_row($top_week)) {

            if ($top ['song_id']) {
                $row = $db->super_query("SELECT vass_songs.artist_id, vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
			vass_artists.id AS artist_id, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id 
			FROM vass_songs LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN 
			vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_songs.id = '" . $top ['song_id'] . "'");
                $songs ['album'] = $row ['song_album'];
                $songs ['artist_id'] = $row ['artist_id'];
                $songs ['similar_artists'] = similar_artists($row ['song_id']);
                $songs ['buy_link'] = null;
                $songs ['artist'] = $row ['song_artist'];
//                    $songs ['url'] = stream($row ['song_id']);
                $songs ['image'] = songlist_images($row ['album_id']);
                $songs ['title'] = $row ['song_title'];
                $songs ['metadata_state'] = metadata_state($row ['song_id']);
                $songs ['sources'] = sources($row ['song_id']);
                $songs ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                $songs ['last_loved'] = null;
                $songs ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                $songs ['aliases'] = aliases($row ['song_id']);
                $songs ['loved_count'] = $row ['loved'];
                $songs ['id'] = $row ['song_id'];
                $songs ['tags'] = tags($row ['song_id']);
                $songs ['trending_rank_today'] = trending_rank_today($row ['song_id']);
                $songs ['user_love'] = "";
                $result ['year-songs'] [] = $songs;
            }
        }

        if ($songs == null) {
            $result ['year-songs'] = array();
        }
        $result ['year-total'] = $total_results['count'];



        $D_SORT = 800;

        $total_results = $db->super_query("SELECT COUNT(*) AS count FROM (SELECT COUNT(*) AS bit, song_id FROM vass_analz WHERE `time` > '" . date("Y-m-d", (time() - $D_SORT * 24 * 3600)) . "' GROUP BY song_id ORDER by bit DESC LIMIT 40) AS count");
        $top_week = $db->query("SELECT COUNT(*) AS count, song_id FROM vass_analz WHERE `time` > '" . date("Y-m-d", (time() - $D_SORT * 24 * 3600)) . "' GROUP BY song_id ORDER by count DESC LIMIT 40 ");

//                              print_r( $top_week);
//                die;  
        while ($top = $db->get_row($top_week)) {

            if ($top ['song_id']) {
                $row = $db->super_query("SELECT vass_songs.artist_id, vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
			vass_artists.id AS artist_id, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id 
			FROM vass_songs LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN 
			vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_songs.id = '" . $top ['song_id'] . "'");
                $songs ['album'] = $row ['song_album'];
                $songs ['artist_id'] = $row ['artist_id'];
                $songs ['similar_artists'] = similar_artists($row ['song_id']);
                $songs ['buy_link'] = null;
                $songs ['artist'] = $row ['song_artist'];
//                    $songs ['url'] = stream($row ['song_id']);
                $songs ['image'] = songlist_images($row ['album_id']);
                $songs ['title'] = $row ['song_title'];
                $songs ['metadata_state'] = metadata_state($row ['song_id']);
                $songs ['sources'] = sources($row ['song_id']);
                $songs ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                $songs ['last_loved'] = null;
                $songs ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                $songs ['aliases'] = aliases($row ['song_id']);
                $songs ['loved_count'] = $row ['loved'];
                $songs ['id'] = $row ['song_id'];
                $songs ['tags'] = tags($row ['song_id']);
                $songs ['trending_rank_today'] = trending_rank_today($row ['song_id']);
                $songs ['user_love'] = "";
                $result ['alltime-songs'] [] = $songs;
            }
        }

        if ($songs == null) {
            $result ['alltime-songs'] = array();
        }
        $result ['alltime-total'] = $total_results['count'];


        $result ['status_text'] = "OK";
        $result ['status_code'] = 200;

//            $result ['next'] = $next;
//            $result ['previous'] = $previous;
//        } else {
//            $result ['status_code'] = 402;
//
//            $result ['status_text'] = "Page parameter wrong...";
//        }
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $result ['status_code'] = 401;

        $result ['status_text'] = "Authentication required.";
    }
    print json_encode($result);
} elseif ($type == "aotw") {

    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $album = $db->super_query("SELECT vass_albums.name AS album_title, vass_albums.descr, vass_albums.date, vass_artists.name AS artist_name, vass_albums.id AS album_id FROM vass_albums LEFT JOIN vass_artists ON vass_albums.artist_id = vass_artists.id WHERE vass_albums.id='" . $config['album_week'] . "'");

        $sql_result = $db->query("SELECT vass_songs.id AS song_id, vass_songs.title AS song_title, vass_songs.loved, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id FROM vass_songs LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_albums.id = '" . $config['album_week'] . "'");

        while ($row = $db->get_row($sql_result)) {

            $song_list ['album'] = $row ['song_album'];
            $song_list ['similar_artists'] = similar_artists($row ['song_id']);
            $song_list ['buy_link'] = null;
            $song_list ['artist'] = $row ['song_artist'];
//            $song_list ['url'] = stream($row ['song_id']);
            $song_list ['image'] = songlist_images($row ['album_id']);
            $song_list ['title'] = $row ['song_title'];
            $song_list ['metadata_state'] = metadata_state($row ['song_id']);
            $song_list ['sources'] = sources($row ['song_id']);
            $song_list ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
            $song_list ['last_loved'] = null;
            $song_list ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
            $song_list ['aliases'] = aliases($row ['song_id']);
            $song_list ['loved_count'] = $row ['loved'];
            $song_list ['id'] = $row ['song_id'];
            $song_list ['tags'] = tags($row ['song_id']);
            $song_list ['trending_rank_today'] = trending_rank_today($row ['song_id']);
            $songs [] = $song_list;
        }

        $buffer = array("status_text" => "OK", "status_code" => 200, "results" => 1, "start" => 0, "total" => 1, "albums" => array("description" => $album ['descr'], "artist" => $album ['artist_name'], "date" => date('D M d Y H:i:s O', strtotime($album ['date'])), "artwork_url" => $config['siteurl'] . "static/albums/" . $config['album_week'] . "_extralarge.jpg", "title" => $album ['album_title'], "day" => 20111005, "songs" => $songs));
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "last_loved") {

    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $sql_result = $db->query("SELECT DISTINCT vass_songs.id AS song_id, vass_songs.title AS song_title, vass_songs.loved, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id, vass_song_love.created_on, vass_users.username, vass_users.user_id FROM vass_song_love LEFT JOIN vass_friendship ON vass_friendship.follower_id = vass_song_love.user_id LEFT JOIN vass_songs ON vass_song_love.song_id = vass_songs.id LEFT JOIN vass_users ON vass_song_love.user_id = vass_users.user_id LEFT JOIN vass_albums on vass_songs.album_id = vass_albums.id LEFT JOIN vass_artists ON vass_songs.artist_id = vass_artists.id ORDER BY vass_song_love.id DESC");

        $start = $_REQUEST ['start'];

        $page_start = $start;

        $page_end = $start + 20;

        $total_results = $db->num_rows($sql_result);

        $i = 0;

        while ($row = $db->get_row($sql_result)) {

            if ($i >= $page_start) {

                $object ['title'] = $row ['song_title'];
                $object ['object'] ['album'] = $row ['song_album'];
                $object ['object'] ['similar_artists'] = similar_artists($row ['song_id']);
                $object ['object'] ['buy_link'] = null;
                $object ['object'] ['artist'] = $row ['song_artist'];
//                $object ['object'] ['url'] = stream($row ['song_id']);
                $object ['object'] ['image'] = songlist_images($row ['album_id']);
                $object ['object'] ['title'] = $row ['song_title'];
                $object ['object'] ['metadata_state'] = metadata_state($row ['song_id']);
                $object ['object'] ['sources'] = sources($row ['song_id']);
                $object ['object'] ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                $object ['object'] ['last_loved'] = null;
                $object ['object'] ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                $object ['object'] ['aliases'] = aliases($row ['song_id']);
                $object ['object'] ['loved_count'] = $row ['loved'];
                $object ['object'] ['id'] = $row ['song_id'];
                $object ['object'] ['tags'] = tags($row ['song_id']);
                $object ['object'] ['trending_rank_today'] = trending_rank_today($row ['song_id']);
                $object ['object'] ['user_love'] = array("username" => $row ['username'], "created_on" => date('D M d Y H:i:s O', strtotime($row ['created_on'])));
                $activities [] = $object;
            }
            $i ++;

            if ($i >= $page_end)
                break;
        }

        $buffer ['status_text'] = "OK";
        $buffer ['status_code'] = "200";
        $buffer ['results'] = $total_results;
        $buffer ['start'] = $start;
        $buffer ['total'] = $total_results;
        $buffer ['activities'] = $activities;
    }else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "genre") {

    $name = $db->safesql($_REQUEST ['name']);
    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $row = $db->super_query("SELECT id FROM vass_genres WHERE name LIKE '%$name%' LIMIT 0,1");

        $page = intval($_REQUEST['page']);

        if (!empty($page) && $page != 0) {

            $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_songs LEFT JOIN vass_albums ON 
	vass_songs.album_id = vass_albums.id LEFT JOIN vass_artists ON vass_songs.artist_id = vass_artists.id 
	WHERE vass_artists.tag REGEXP '[[:<:]]" . $row ['id'] . "[[:>:]]'");

            $limit = 20;
            $newpage = $page * $limit;

            if ($page > 1 && $newpage <= $total_results['count']) {
                echo "test";
                die;
                $previous = $page - 1;
            }
            if ($newpage < $total_results['count']) {
                $next = $page + 1;
            }
            if ($page > 1) {
                $page = ($page - 1) * $limit;
            } else {
                $page = 0;
            }





            $sql_result = $db->query("SELECT vass_songs.artist_id, vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
	vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id FROM vass_songs LEFT JOIN vass_albums ON 
	vass_songs.album_id = vass_albums.id LEFT JOIN vass_artists ON vass_songs.artist_id = vass_artists.id 
	WHERE vass_artists.tag REGEXP '[[:<:]]" . $row ['id'] . "[[:>:]]' LIMIT $limit OFFSET $page");


            while ($row = $db->get_row($sql_result)) {

                $songs ['album'] = $row ['song_album'];
                $songs ['artist_id'] = $row ['artist_id'];
                $songs ['similar_artists'] = similar_artists($row ['song_id']);
                $songs ['buy_link'] = null;
                $songs ['artist'] = $row ['song_artist'];
//                $songs ['url'] = stream($row ['song_id']);
                $songs ['image'] = songlist_images($row ['album_id']);
                $songs ['title'] = $row ['song_title'];
                $songs ['metadata_state'] = metadata_state($row ['song_id']);
                $songs ['sources'] = sources($row ['song_id']);
                $songs ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                $songs ['last_loved'] = null;
                $songs ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                $songs ['aliases'] = aliases($row ['song_id']);
                $songs ['loved_count'] = $row ['loved'];
                $songs ['id'] = $row ['song_id'];
                $songs ['tags'] = tags($row ['song_id']);
                $songs ['trending_rank_today'] = trending_rank_today($row ['song_id']);
                $songs ['user_love'] = null;
                $result ['songs'] [] = $songs;
            }

            $result ['status_text'] = "OK";
            $result ['status_code'] = 200;

            $result ['total'] = $total_results['count'];
            $result['next'] = $next;
            $result['previous'] = $previous;
        } else {
            $result ['status_code'] = 402;

            $result ['status_text'] = "Page parameter wrong....";
        }
    } else {

        header("HTTP/1.0 401 UNAUTHORIZED");

        $result ['status_code'] = 401;

        $result ['status_text'] = "Authentication required.";
    }
    print json_encode($result);
} elseif ($type == "member") {

    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");



    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $member = $db->query("SELECT * FROM vass_users WHERE user_id = '" . $user_id . "'");

        $member_id = $db->get_row($member);

        $username = $db->safesql($_REQUEST ['username']);

        $username = preg_replace("/[^a-zA-Z0-9\s]/", "", $username);

        $action = $db->safesql($_REQUEST ['action']);

        if ($action == "loved") {

            $row = $db->super_query("SELECT user_id FROM vass_users WHERE username = '" . $username . "'");

            $sql_result = $db->query("SELECT vass_songs.artist_id, vass_songs.id AS song_id, vass_songs.title AS song_title, vass_songs.loved, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id, vass_song_love.created_on, vass_users.username, vass_users.user_id FROM vass_song_love LEFT JOIN vass_songs ON vass_song_love.song_id = vass_songs.id LEFT JOIN vass_users ON vass_song_love.user_id = vass_users.user_id LEFT JOIN vass_albums on vass_songs.album_id = vass_albums.id LEFT JOIN vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_song_love.user_id = '" . $row ['user_id'] . "' ORDER BY vass_song_love.id DESC");

//            $start = $_REQUEST ['start'];
//
//            $page_start = $start;
//
//            $page_end = $start + 20;

            $total_results = $db->num_rows($sql_result);

            $i = 0;

            while ($row = $db->get_row($sql_result)) {


                $songs ['album'] = $row ['song_album'];
                $songs ['artist_id'] = $row ['artist_id'];
                $songs ['similar_artists'] = similar_artists($row ['song_id']);
                $songs ['buy_link'] = null;
                $songs ['artist'] = $row ['song_artist'];
//                    $songs ['url'] = stream($row ['song_id']);
                $songs ['image'] = songlist_images($row ['album_id']);
                $songs ['title'] = $row ['song_title'];
                $songs ['metadata_state'] = metadata_state($row ['song_id']);
                $songs ['sources'] = sources($row ['song_id']);
                $songs ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                $songs ['last_loved'] = null;
                $songs ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                $songs ['aliases'] = aliases($row ['song_id']);
                $songs ['loved_count'] = $row ['loved'];
                $songs ['id'] = $row ['song_id'];
                $songs ['tags'] = tags($row ['song_id']);
                $songs ['trending_rank_today'] = trending_rank_today($row ['song_id']);
                $songs ['user_love'] = array("username" => $member_id ['username']);
                $buffer ['songs'] [] = $songs;


                $i ++;
            }

            $buffer ['status_text'] = "OK";
            $buffer ['status_code'] = "200";


            $buffer ['total'] = $total_results;

            print json_encode($buffer);
        } elseif ($action == "feedlove") {

            $row = $db->super_query("SELECT user_id FROM vass_users WHERE username = '" . $username . "'");

            if ($username == "tastemakers") {

                $sql_result = $db->query("SELECT DISTINCT vass_songs.id AS song_id, vass_songs.title AS song_title, vass_songs.loved, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id, vass_song_love.created_on, vass_users.username, vass_users.user_id FROM vass_song_love LEFT JOIN vass_friendship ON vass_friendship.follower_id = vass_song_love.user_id LEFT JOIN vass_songs ON vass_song_love.song_id = vass_songs.id LEFT JOIN vass_users ON vass_song_love.user_id = vass_users.user_id LEFT JOIN vass_albums on vass_songs.album_id = vass_albums.id LEFT JOIN vass_artists ON vass_songs.artist_id = vass_artists.id");
            } else {

                $row = $db->super_query("SELECT user_id FROM vass_users WHERE username = '" . $username . "'");

                $sql_result = $db->query("SELECT DISTINCT vass_songs.id AS song_id, vass_songs.title AS song_title, vass_songs.loved, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id, vass_song_love.created_on, vass_users.username, vass_users.user_id FROM vass_song_love LEFT JOIN vass_friendship ON vass_friendship.follower_id = vass_song_love.user_id LEFT JOIN vass_songs ON vass_song_love.song_id = vass_songs.id LEFT JOIN vass_users ON vass_song_love.user_id = vass_users.user_id LEFT JOIN vass_albums on vass_songs.album_id = vass_albums.id LEFT JOIN vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_friendship.user_id = '" . $row ['user_id'] . "' ORDER BY vass_song_love.id DESC");
            }
//            $start = $_REQUEST ['start'];
//
//            $page_start = $start;
//
//            $page_end = $start + 20;

            $total_results = $db->num_rows($sql_result);

            $i = 0;

            while ($row = $db->get_row($sql_result)) {


                $object ['title'] = $row ['song_title'];
                $object ['object'] ['album'] = $row ['song_album'];
                $object ['object'] ['similar_artists'] = similar_artists($row ['song_id']);
                $object ['object'] ['buy_link'] = null;
                $object ['object'] ['artist'] = $row ['song_artist'];
//                    $object ['object'] ['url'] = stream($row ['song_id']);
                $object ['object'] ['image'] = songlist_images($row ['album_id']);
                $object ['object'] ['title'] = $row ['song_title'];
                $object ['object'] ['metadata_state'] = metadata_state($row ['song_id']);
                $object ['object'] ['sources'] = sources($row ['song_id']);
                $object ['object'] ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                $object ['object'] ['last_loved'] = null;
                $object ['object'] ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                $object ['object'] ['aliases'] = aliases($row ['song_id']);
                $object ['object'] ['loved_count'] = $row ['loved'];
                $object ['object'] ['id'] = $row ['song_id'];
                $object ['object'] ['tags'] = tags($row ['song_id']);
                $object ['object'] ['trending_rank_today'] = trending_rank_today($row ['song_id']);
                $object ['object'] ['user_love'] = array("username" => $row ['username'], "created_on" => date('D M d Y H:i:s O', strtotime($row ['created_on'])));
                $activities [] = $object;
                /*
                 * $buffer .= '"user_love": { "username": ' . json_encode (
                 * $user_love ) . ', "comment": "", "context": "", "source": ' .
                 * json_encode ( $config['siteurl'] . 'song/' . $song_id ) . ',
                 * "created_on": "' . date( 'D M d Y H:i:s O', strtotime(
                 * $user_love_on ) ) . '", "client_id": "lala_web" },
                 */


                $i ++;
            }

            $buffer ['status_text'] = "OK";
            $buffer ['status_code'] = "200";
//            $buffer ['results'] = $total_results;
//            $buffer ['start'] = $start;
            $buffer ['total'] = $total_results;
            $buffer ['activities'] = $activities;

            print json_encode($buffer);
        } elseif ($action == "following") {

            $row = $db->super_query("SELECT user_id FROM vass_users WHERE username = '" . $username . "' LIMIT 0,1");

            $sql_result = $db->query("SELECT vass_friendship.follower_id, vass_users.username, vass_users.name, vass_users.bio, vass_users.website, vass_users.total_loved, vass_users.location, vass_users.total_loved, vass_users.total_following, vass_users.total_followers, vass_users.avatar, vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_users LEFT JOIN vass_background ON vass_users.user_id = vass_background.user_id LEFT JOIN vass_friendship ON vass_users.user_id = vass_friendship.follower_id WHERE vass_friendship.user_id = '" . $row ['user_id'] . "';");

//            $start = $_REQUEST ['start'];
//
//            $page_start = $start;
//
//            $page_end = $start + 20;

            $total_results = $db->num_rows($sql_result);

            $i = 0;

            while ($result = $db->get_row($sql_result)) {



                $buffer = $result;
                $buffer ['is_beta_tester'] = false;
                $buffer ['viewer_following'] = viewer_following($result ['follower_id']);
                $buffer ['import_feeds'] = import_feeds($result ['user_id']);
                $buffer ['image'] = avatar($result ['avatar'], $result ['username']);

                $row = $db->super_query("SELECT vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_background WHERE vass_background.user_id = '" . $result ['user_id'] . "';");

                if ($row ['image']) {
                    $use_image = true;
                    $is_default = false;
                } else {
                    $is_default = true;
                    $use_image = false;
                }

                $buffer ['background'] = $row;
                $buffer ['background'] ['is_default'] = $is_default;
                $buffer ['background'] ['use_image'] = $use_image;

                unset($buffer ['password']);

                $following [] = $buffer;

                $i ++;
            }

            $buffer = array("status_code" => 200, "status_text" => "OK", "following" => $following, "total" => $total_results);

            print json_encode($buffer);
        } elseif ($action == "followers") {

            $row = $db->super_query("SELECT user_id FROM vass_users WHERE username = '" . $username . "' LIMIT 0,1");

            $sql_result = $db->query("SELECT vass_users.user_id, vass_users.username, vass_users.name, vass_users.bio, vass_users.website, vass_users.total_loved, vass_users.location, vass_users.total_loved, vass_users.total_following, vass_users.total_followers, vass_users.avatar, vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_users LEFT JOIN vass_background ON vass_users.user_id = vass_background.user_id LEFT JOIN vass_friendship ON vass_users.user_id = vass_friendship.user_id WHERE vass_friendship.follower_id = '" . $row ['user_id'] . "';");
//
//            $start = $_REQUEST ['start'];
//
//            $page_start = $start;
//
//            $page_end = $start + 20;

            $total_results = $db->num_rows($sql_result);

            $i = 0;

            while ($result = $db->get_row($sql_result)) {



                $buffer = $result;
                $buffer ['is_beta_tester'] = false;
                $buffer ['viewer_following'] = viewer_following($result ['user_id']);
                $buffer ['import_feeds'] = import_feeds($result ['user_id']);
                $buffer ['image'] = avatar($result ['avatar'], $result ['username']);

                $row = $db->super_query("SELECT vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_background WHERE vass_background.user_id = '" . $result ['user_id'] . "';");

                if ($row ['image']) {
                    $use_image = true;
                    $is_default = false;
                } else {
                    $is_default = true;
                    $use_image = false;
                }

                $buffer ['background'] = $row;
                $buffer ['background'] ['is_default'] = $is_default;
                $buffer ['background'] ['use_image'] = $use_image;

                unset($buffer ['password']);

                $followers [] = $buffer;

                $i ++;

                if ($i >= $page_end)
                    break;
            }

            $buffer = array("status_code" => 200, "status_text" => "OK", "followers" => $followers, "total" => $total_results);

            print json_encode($buffer);
        } elseif ($action == "tastemakers") {

            $sql_result = $db->query("SELECT DISTINCT vass_friendship.follower_id, vass_users.username, vass_users.name, vass_users.bio, vass_users.website, vass_users.total_loved, vass_users.location, vass_users.total_loved, vass_users.total_following, vass_users.total_followers, vass_users.avatar FROM vass_users LEFT JOIN vass_friendship ON vass_users.user_id = vass_friendship.follower_id");

            $total_results = $db->num_rows($sql_result);

            while ($result = $db->get_row($sql_result)) {


                $buffer = $result;
                $buffer ['is_beta_tester'] = false;
                $buffer ['viewer_following'] = viewer_following($result ['user_id']);
                $buffer ['import_feeds'] = import_feeds($result ['user_id']);
                $buffer ['image'] = avatar($result ['avatar'], $result ['username']);

                $row = $db->super_query("SELECT vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_background WHERE vass_background.user_id = '" . $result ['user_id'] . "';");

                if ($row ['image']) {
                    $use_image = true;
                    $is_default = false;
                } else {
                    $is_default = true;
                    $use_image = false;
                }

                $row['is_default'] = $is_default;
                $row['use_image'] = $use_image;
                $buffer ['background'] = $row;
                $following [] = $buffer;
            }

            if (!$following)
                $following = "";

            $buffer = array("status_code" => 200, "status_text" => "OK", "following" => $following, "total" => $total_results);

            print json_encode($buffer);
        } elseif ($action == "notifications") {

            $buffer = '{
			    "status_text": "OK", 
			    "status_code": 200, 
			    "results": 0, 
			    "sites": [], 
			    "start": 0, 
			    "total": 0
			}';
        } elseif ($action == "playlist") {

            $row = $db->super_query("SELECT user_id FROM vass_users WHERE username = '" . $username . "' LIMIT 0,1");

            $sql_result = $db->query("SELECT vass_playlists.name, vass_playlists.date, vass_playlists.id AS playlist_id, vass_playlists.cover, vass_playlists.descr,
		vass_users.username 
		FROM vass_playlists LEFT JOIN vass_users ON vass_playlists.user_id = vass_users.user_id WHERE vass_playlists.user_id = '" . $row ['user_id'] . "';");

//            $start = $_REQUEST ['start'];
//
//            $page_start = $start;
//
//            $page_end = $start + 20;

            $total_results = $db->num_rows($sql_result);

            $i = 0;

            while ($result = $db->get_row($sql_result)) {



                $buffer = $result;

                $playlists [] = $buffer;

                $i ++;
            }

            $buffer = array("status_code" => 200, "status_text" => "OK", "playlists" => $playlists, "total" => $total_results);

            print json_encode($buffer);
        } elseif ($username) {

            $row = $db->super_query("SELECT vass_friendship.user_id, vass_users.user_id, vass_users.name, vass_users.bio, vass_users.website, vass_users.total_loved, vass_users.location, vass_users.total_loved, vass_users.total_following, vass_users.total_followers, vass_users.avatar, vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_users LEFT JOIN vass_background ON vass_users.user_id = vass_background.user_id LEFT JOIN vass_friendship ON vass_users.user_id = vass_friendship.follower_id WHERE vass_users.username = '" . $username . "';");

            if (!$row ['user_id']) {

                header('HTTP/1.0 403 Not Found');
                $buffer ['status_code'] = 400;
                $buffer ['status_text'] = "Unknown user {$username}.";
            } else {

                $buffer ['status_code'] = 200;

                $buffer ['status_text'] = "OK";

                $buffer ['user'] = $row;
                $buffer ['user'] ['is_beta_tester'] = false;
                $buffer ['user'] ['viewer_following'] = viewer_following($row ['user_id']);
                $buffer ['user'] ['import_feeds'] = import_feeds($row ['user_id']);
                $buffer ['user'] ['image'] = avatar($row ['avatar'], $username);
                $total_playlist = $db->super_query("SELECT COUNT(*) AS count FROM vass_playlists WHERE user_id = '" . $member_id['user_id'] . "';");
                $buffer ['user'] ['total_playlist'] = $total_playlist['count'];

                $row = $db->super_query("SELECT vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_background WHERE vass_background.user_id = '" . $row ['user_id'] . "';");

                if ($row ['image']) {
                    $use_image = true;
                    $is_default = false;
                } else {
                    $is_default = true;
                    $use_image = false;
                }

                $buffer ['user'] ['background'] = $row;
                $buffer ['user'] ['background'] ['is_default'] = $is_default;
                $buffer ['user'] ['background'] ['use_image'] = $use_image;

                unset($buffer ['user'] ['password']);
            }

            print json_encode($buffer);
        }
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";

        print json_encode($buffer);
    }

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');
} elseif ($type == "profile") {

    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    $member = $db->query("SELECT * FROM vass_users WHERE user_id = '" . $user_id . "'");

    $member_id = $db->get_row($member);

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {



        $username = $db->safesql($_REQUEST ['username']);

        $username = preg_replace("/[^a-zA-Z0-9\s]/", "", $username);

        $action = $db->safesql($_REQUEST ['action']);

        if ($action == "maybe-friends") {

            $buffer = array("status_code" => 200, "status_text" => "OK", "results" => 20, "start" => $start, "users" => array(), "total" => $total_results);
        } elseif ($action == "notifications") {

            $buffer = array("status_code" => 200, "status_text" => "OK", "results" => 20, "start" => $start, "sites" => array(), "total" => $total_results);
        } elseif ($action == "follow") {

            $row = $db->super_query("SELECT user_id, avatar FROM vass_users WHERE username = '" . $username . "' LIMIT 0,1");

            $db->query("INSERT IGNORE INTO vass_friendship SET user_id = '" . $member_id ['user_id'] . "', follower_id = '" . $row ['user_id'] . "'");

            $db->query("UPDATE vass_users SET total_following  = total_following+1 WHERE user_id = '" . $member_id ['user_id'] . "'");

            $db->query("UPDATE vass_users SET total_followers  = total_followers+1 WHERE user_id = '" . $row ['user_id'] . "'");

            $buffer = array("status_code" => 200, "status_text" => "OK", "user" => array("username" => $username, "image" => avatar($row ['avatar'], $username)));
        } elseif ($action == "unfollow") {

            $row = $db->super_query("SELECT user_id, avatar FROM vass_users WHERE username = '" . $username . "' LIMIT 0,1");

            $db->query("DELETE FROM vass_friendship WHERE user_id = '" . $member_id ['user_id'] . "' AND follower_id = '" . $row ['user_id'] . "'");

            $db->query("UPDATE vass_users SET total_following  = total_following-1 WHERE user_id = '" . $member_id ['user_id'] . "'");

            $db->query("UPDATE vass_users SET total_followers  = total_followers-1 WHERE user_id = '" . $row ['user_id'] . "'");

            $buffer = array("status_code" => 200, "status_text" => "OK", "user" => array("username" => $username, "image" => avatar($row ['avatar'], $username)));
        } else {

            $buffer ['status_code'] = 200;

            $buffer ['status_text'] = "OK";

            $buffer ['user'] = $member_id;
            $buffer ['user'] ['is_beta_tester'] = false;
            $buffer ['user'] ['viewer_following'] = viewer_following($member_id ['user_id']);
            $buffer ['user'] ['import_feeds'] = import_feeds($member_id ['user_id']);
            $buffer ['user'] ['image'] = avatar($member_id ['avatar'], $member_id ['username']);

            $row = $db->super_query("SELECT vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_background WHERE vass_background.user_id = '" . $member_id ['user_id'] . "';");

            if ($row ['image']) {
                $use_image = "true";
                $is_default = "false";
            } else {
                $is_default = "true";
                $use_image = "false";
            }

            $buffer ['user'] ['background'] = $row;
            $buffer ['user'] ['background'] ['is_default'] = $is_default;
            $buffer ['user'] ['background'] ['use_image'] = $use_image;

            unset($buffer ['user'] ['password']);
        }
    } else {

        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "me") {

    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    $member = $db->query("SELECT * FROM vass_users WHERE user_id = '" . $user_id . "'");

    $member_id = $db->get_row($member);

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $buffer ['status_code'] = 200;

        $buffer ['status_text'] = "OK";

        $buffer ['user'] = $member_id;
        $buffer ['user'] ['is_beta_tester'] = false;
        $buffer ['user'] ['viewer_following'] = false;
        $buffer ['user'] ['import_feeds'] = import_feeds($member_id ['user_id']);
        $buffer ['user'] ['image'] = avatar($member_id ['avatar'], $member_id ['username']);

        $row = $db->super_query("SELECT vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_background WHERE vass_background.user_id = '" . $member_id ['user_id'] . "';");

        if ($row ['image']) {
            $use_image = true;
            $is_default = false;
        } else {
            $is_default = true;
            $use_image = false;
        }

        $buffer ['user'] ['background'] = $row;
        $buffer ['user'] ['background'] ['is_default'] = $is_default;
        $buffer ['user'] ['background'] ['use_image'] = $use_image;

        unset($buffer ['user'] ['password']);
        unset($buffer ['user'] ['email']);
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "trending") {


    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");


    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {


        $page = intval($_REQUEST['page']);

        if (!empty($page) && $page != 0) {
            $_DATE = date("d", time());

            $genre = $db->safesql($_REQUEST['genre']);

            $genre_id = $db->super_query("SELECT id FROM vass_genres WHERE name LIKE '%" . $genre . "%'");

            $trending_day = $db->query("SELECT COUNT(*) AS count, song_id FROM vass_analz WHERE `time` > '" . date("Y-m-d", (time() - 7 * 24 * 3600)) . "' GROUP BY song_id ORDER by count DESC  ");

//        echo"<pre>";
//        print_r($trending_day);
//        echo"<pre>";
//        die;
            $count = 0;
            while ($sim = $db->get_row($trending_day)) {

                $count++;
            }


            $limit = 20;
            $newpage = $page * $limit;

            if ($page > 1 && $newpage <= $count) {
                $previous = $page - 1;
            }
            if ($page > 1 && $newpage < $count) {
                $next = $page + 1;
            }
            if ($page > 1) {
                $page = ($page - 1) * $limit;
            } else {
                $page = 0;
            }

            $trending_day = $db->query("SELECT COUNT(*) AS count, song_id FROM vass_analz WHERE `time` > '" . date("Y-m-d", (time() - 7 * 24 * 3600)) . "' GROUP BY song_id ORDER by count DESC LIMIT $limit OFFSET $page ");
            $i = 1;

            while ($trending = $db->get_row($trending_day)) {
//echo"<pre>";print_r($trending);echo"<pre>";die;
                if (!empty($genre)) {
                    $row = $db->super_query("SELECT vass_songs.id song_id, vass_songs.artist_id, 
				vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
				vass_artists.id AS artist_id, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id 
				FROM vass_songs LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN 
					vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_songs.id = '" . $trending ['song_id'] . "' AND vass_songs.tags REGEXP '[[:<:]]" . $genre_id['id'] . "[[:>:]]'");
                } else {
                    $row = $db->super_query("SELECT vass_songs.id AS song_id, vass_songs.artist_id, 
				vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
				vass_artists.id AS artist_id, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id 
				FROM vass_songs LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN 
				vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_songs.id = '" . $trending ['song_id'] . "'");
                }


                if ($row ['song_id']) {
                    $songs ['album'] = $row ['song_album'];
                    $songs ['artist_id'] = $row ['artist_id'];
                    $songs ['similar_artists'] = similar_artists($row ['song_id']);
                    $songs ['buy_link'] = null;
                    $songs ['artist'] = $row ['song_artist'];
//                $songs ['url'] = stream($row ['song_id']);
//                echo $songs ['url'];die;
                    $songs ['image'] = songlist_images($row ['album_id']);
                    $songs ['artist_image'] = artist_images($row ['album_id'], $row ['artist_id']);
                    $songs ['title'] = $row ['song_title'];
                    $songs ['metadata_state'] = metadata_state($row ['song_id']);
                    $songs ['sources'] = sources($row ['song_id']);
                    $songs ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                    $songs ['last_loved'] = null;
                    $songs ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                    $songs ['aliases'] = aliases($row ['song_id']);
                    $songs ['loved_count'] = $row ['loved'];
                    $songs ['id'] = $row ['song_id'];
                    $songs ['tags'] = tags($row ['song_id']);
                    $songs ['trending_rank_today'] = $i;
                    $result ['songs'] [] = $songs;
                } else
                    $result ['songs'] = array();
                $i ++;
            }

            $result ['trending_date'] = date("Y-m-d", time());
            $result ['status_text'] = "OK";
            $result ['status_code'] = 200;
            $result ['total'] = $count;
            $result['next'] = $next;
            $result['previous'] = $previous;

            if ($result && $i > 19)
                cache("trending/" . $_DATE, json_encode($result));
        }else {
            $result ['status_code'] = 402;


            $result ['status_text'] = "Page parameter is wrong ....";
        }
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $result ['status_code'] = 401;

        $result ['status_text'] = "Authentication required.";
    }

    print json_encode($result);

    //} else {
    //	echo $trending_json;
    //}
} elseif ($type == "search") {

    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");


    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {




        $qtxt = $_REQUEST [q];

        $qtxt = (strtolower($qtxt));

        $qtxt = makekeyword($qtxt);

        $sql_result = $db->query("SELECT DISTINCT vass_songs.artist_id, vass_songs.id AS song_id, vass_songs.title AS song_title, vass_songs.loved, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id FROM vass_songs LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN vass_artists ON vass_songs.artist_id = vass_artists.id WHERE LOWER(vass_songs.title) LIKE '%$qtxt%' or LOWER(vass_artists.name) LIKE '%$qtxt%' or LOWER(vass_albums.name) LIKE '%$qtxt%'");

        $start = $_REQUEST ['start'];

        $page_start = $start;

        $page_end = $start + 20;

        $total_results = $db->num_rows($sql_result);

        $i = 0;

        while ($row = $db->get_row($sql_result)) {

            if ($i >= $page_start) {

                $songs ['album'] = $row ['song_album'];
                $songs ['artist_id'] = $row ['artist_id'];
                $songs ['similar_artists'] = similar_artists($row ['song_id']);
                $songs ['buy_link'] = null;
                $songs ['artist'] = $row ['song_artist'];
//                $songs ['url'] = stream($row ['song_id']);
                $songs ['image'] = songlist_images($row ['album_id']);
                $songs ['title'] = $row ['song_title'];
                $songs ['metadata_state'] = metadata_state($row ['song_id']);
                $songs ['sources'] = sources($row ['song_id']);
                $songs ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                $songs ['last_loved'] = null;
                $songs ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                $songs ['aliases'] = aliases($row ['song_id']);
                $songs ['loved_count'] = $row ['loved'];
                $songs ['id'] = $row ['song_id'];
                $songs ['tags'] = tags($row ['song_id']);
                $songs ['trending_rank_today'] = trending_rank_today($row ['song_id']);
                $result ['songs'] [] = $songs;
            }

            $i ++;

            if ($i >= $page_end)
                break;
        }

        $result ['status_text'] = "OK";
        $result ['status_code'] = "200";
        $result ['results'] = $total_results;
        $result ['start'] = $start;
        $result ['total'] = $total_results;

        $db->close();
    }else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $result ['status_code'] = 401;

        $result ['status_text'] = "Authentication required.";
    }

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($result);
} elseif ($type == "love") {

    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $songid = $db->safesql($_REQUEST ['songid']);

        $action = $db->safesql($_REQUEST ['action']);

        if ($action == "love") {

            $song_id = $db->safesql($_REQUEST ['songid']);

            $db->query("INSERT IGNORE INTO vass_song_love SET song_id = '" . $songid . "', user_id= '" . $user_id . "', created_on = '" . date("Y-m-d H:i:s", time()) . "'");

            $db->query("UPDATE vass_songs SET loved = loved+1, last_loved = '" . date("Y-m-d H:i:s", time()) . "' WHERE id = '" . $songid . "'");

            $db->query("UPDATE vass_users SET total_loved = total_loved+1 WHERE user_id= '" . $user_id . "'");

            $buffer ['status_code'] = 200;

            $buffer ['status_text'] = "Added song to lover.";
        } elseif ($action == "unlove") {

            $db->query("DELETE FROM vass_song_love WHERE song_id = '" . $songid . "' AND user_id= '" . $user_id . "'");

            $db->query("UPDATE vass_songs SET loved = loved-1 WHERE id = '" . $songid . "'");

            $db->query("UPDATE vass_users SET total_loved = total_loved-1 WHERE user_id= '" . $user_id . "'");

            $buffer ['status_code'] = 200;

            $buffer ['status_text'] = "removed song to lover.";

            $buffer ['song'] ['id'] = $songid;
        }
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }

    $db->close();

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "now_playing") {

    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $song_id = intval($_REQUEST ['songid']);

        $db->query("UPDATE vass_songs SET played=played+1 WHERE id = '" . $song_id . "'");

        $db->query("INSERT INTO vass_analz SET `time`= '$_TIME', song_id = '" . $song_id . "'");

        $buffer = array("status_code" => 200);
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $result ['status_code'] = 401;

        $result ['status_text'] = "Authentication required.";
    }


    print json_encode($buffer);
} elseif ($type == "song") {

    if (!$_REQUEST ['songid']) {

        header('HTTP/1.0 404 Not Found');

        $buffer ['status_code'] = 404;

        $buffer ['status_text'] = "NOT FOUND.";
    } else {

        $song_id = $db->safesql($_REQUEST ['songid']);

        $row = $db->super_query("SELECT vass_songs.artist_id, vass_songs.created_on, vass_songs.artist_id, vass_artists.tag AS tags, vass_songs.id AS song_id, vass_songs.title AS song_title, 
		vass_songs.loved, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id 
		FROM vass_songs LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id  LEFT JOIN vass_artists ON 
		vass_songs.artist_id = vass_artists.id WHERE vass_songs.id = '" . $song_id . "' LIMIT 0,1");

        if ($row ['song_title']) {

            $songs ['album'] = $row ['song_album'];
            $songs ['artist_id'] = $row ['artist_id'];
            $songs ['created_on'] = date('D M d Y H:i:s O', strtotime($row ['created_on']));
            $songs ['artist_id'] = $row ['artist_id'];
            $songs ['similar_artists'] = similar_artists($row ['tags']);
            $songs ['buy_link'] = null;
            $songs ['artist'] = $row ['song_artist'];
//            $songs ['url'] = stream($row ['song_id']);
            $songs ['image'] = songlist_images($row ['album_id']);
            $songs ['title'] = $row ['song_title'];
            $songs ['metadata_state'] = metadata_state($row ['song_id']);
            $songs ['sources'] = sources($row ['song_id']);
            $songs ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
            $songs ['last_loved'] = null;
            $songs ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
            $songs ['aliases'] = aliases($row ['song_id']);
            $songs ['loved_count'] = $row ['loved'];
            $songs ['id'] = $row ['song_id'];
            $songs ['tags'] = tags($row ['tags']);
            $songs ['trending_rank_today'] = trending_rank_today($row ['song_id']);
            $songs ['user_love'] = null;

            $buffer ['status_code'] = 200;
            $buffer ['status_text'] = "OK";
            $buffer ['song'] = $songs;
        } else {

            header('HTTP/1.0 404 Not Found');

            $buffer ['status_code'] = 404;

            $buffer ['status_text'] = "NOT FOUND.";
        }
    }

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "sotd") {
    echo '{
	    "status_text": "OK", 
	    "status_code": 200, 
	    "results": 1, 
	    "sites": [
	    ], 
	    "start": 0, 
	    "total": 5
	}';
} elseif ($type == "settings") {
    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");



    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $member = $db->query("SELECT * FROM vass_users WHERE user_id = '" . $user_id . "'");

        $member_id = $db->get_row($member);

        $username = $db->safesql($_REQUEST ['username']);

        $username = preg_replace("/[^a-zA-Z0-9\s]/", "", $username);

        $action = $db->safesql($_REQUEST ['action']);

        if ($action == "maybe-friends") {

            $buffer ['status_code'] = 200;

            $buffer ['users'] [] = null;

            $buffer ['status_text'] = "OK";

            $buffer ['results'] = 20;

            $buffer ['start'] = 0;

            $buffer ['total'] = 0;
        } elseif ($action == "tastemakers") {

            $sql_result = $db->query("SELECT vass_users.user_id, vass_users.username, vass_users.name, vass_users.bio, vass_users.website, vass_users.total_loved, vass_users.location, vass_users.total_loved, vass_users.total_following, vass_users.total_followers, vass_users.avatar, vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image
FROM vass_users
LEFT JOIN vass_background ON vass_users.user_id = vass_background.user_id
ORDER BY vass_users.user_id ");

//
//            $start = $_REQUEST['start'];
//
//            $page_start = $start;
//
//            $page_end = $start + 20;

            $total_results = $db->num_rows($sql_result);

            $i = 0;


            while ($result = $db->get_row($sql_result)) {



                $folow = $db->super_query("SELECT follower_id FROM vass_friendship WHERE follower_id = '" . $result['user_id'] . "'");

                $buffer = $result;
                $buffer['is_beta_tester'] = false;
                $buffer['viewer_following'] = viewer_following($folow['follower_id']);
                $buffer['import_feeds'] = import_feeds($result['user_id']);
                $buffer['image'] = avatar($result['avatar'], $result['username']);


                $row = $db->super_query("SELECT vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_background WHERE vass_background.user_id = '" . $result['user_id'] . "';");

                if ($row['image']) {
                    $use_image = true;
                    $is_default = false;
                } else {
                    $is_default = true;
                    $use_image = false;
                }

                $buffer['background'] = $row;
                $buffer['background']['is_default'] = $is_default;
                $buffer['background']['use_image'] = $use_image;

                unset($buffer['password']);


                $following[] = $buffer;

                $i++;
            }


            $buffer = array("status_code" => 200, "status_text" => "OK", "following" => $following, "total" => $total_results);
        } elseif ($action == "search") {

            $keyword = $db->safesql($_REQUEST ['q']);

            $sql_result = $db->query("SELECT DISTINCT vass_friendship.follower_id, vass_users.username, vass_users.name, vass_users.bio, vass_users.website, vass_users.total_loved, vass_users.location, vass_users.total_loved, vass_users.total_following, vass_users.total_followers, vass_users.avatar, vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_users LEFT JOIN vass_background ON vass_users.user_id = vass_background.user_id LEFT JOIN vass_friendship ON vass_users.user_id = vass_friendship.follower_id WHERE vass_users.name LIKE '%$keyword%' or vass_users.email LIKE '%$keyword%' or vass_users.username LIKE '%$keyword%'");

//            $start = $_REQUEST ['start'];
//
//            $page_start = $start;
//
//            $page_end = $start + 20;

            $total_results = $db->num_rows($sql_result);

            $i = 0;

            while ($result = $db->get_row($sql_result)) {



                $buffer = $result;
                $buffer ['is_beta_tester'] = false;
                $buffer ['viewer_following'] = viewer_following($result ['follower_id']);
                $buffer ['import_feeds'] = import_feeds($result ['user_id']);
                $buffer ['image'] = avatar($result ['avatar'], $result ['username']);

                $row = $db->super_query("SELECT vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_background WHERE vass_background.user_id = '" . $result ['user_id'] . "';");

                if ($row ['image']) {
                    $use_image = true;
                    $is_default = false;
                } else {
                    $is_default = true;
                    $use_image = false;
                }

                $buffer ['background'] = $row;
                $buffer ['background'] ['is_default'] = $is_default;
                $buffer ['background'] ['use_image'] = $use_image;

                unset($buffer ['password']);

                $users [] = $buffer;

                $i ++;
            }

            if (!$users)
                $users = "";

            $buffer = array("status_code" => 200, "status_text" => "OK", "users" => $users, "total" => $total_results);
        } elseif ($action == "notifications") {

            $buffer = '{
					    "status_text": "OK", 
					    "status_code": 200, 
					    "results": 0, 
					    "sites": [], 
					    "start": 0, 
					    "total": 0
					}';
        } elseif ($action == "background") {

            $color = $db->safesql($_REQUEST['color']);

            $image = $db->safesql($_REQUEST['image']);

            $position = $db->safesql($_REQUEST['position']);

            $repeat = $db->safesql($_REQUEST['repeat']);

            $use_image = $db->safesql($_REQUEST['use_image']);
            echo $color;
            if ($color)
                $db->query("INSERT INTO vass_background SET `user_id` = '" . $member_id ['user_id'] . "', `color` = '$color', `image` = '$image', `position` = '$position', `repeat` = '$repeat', `use_image` = '$use_image' ON DUPLICATE KEY UPDATE `color` = '$color', `image` = '$image', `position` = '$position', `repeat` = '$repeat', `use_image` = '$use_image';");

            $buffer ['status_code'] = 200;
            $buffer ['status_text'] = "OK";
            $buffer ['user'] = $member_id;
            $buffer ['user'] ['is_beta_tester'] = false;
            $buffer ['user'] ['viewer_following'] = viewer_following($member_id ['user_id']);
            $buffer ['user'] ['import_feeds'] = import_feeds($member_id ['user_id']);
            $buffer ['user'] ['image'] = avatar($member_id ['avatar'], $member_id ['username']);

            $row = $db->super_query("SELECT vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_background WHERE vass_background.user_id = '" . $member_id ['user_id'] . "';");

            if ($row ['image']) {
                $use_image = true;
                $is_default = false;
            } else {
                $is_default = true;
                $use_image = false;
            }

            $buffer ['user'] ['background'] = $row;
            $buffer ['user'] ['background'] ['is_default'] = $is_default;
            $buffer ['user'] ['background'] ['use_image'] = $use_image;

            unset($buffer ['user'] ['password']);
        } elseif ($action == "email") {
            $email = $db->safesql($_REQUEST['email']);
//echo trim($email);die;
            if (trim($email)) {
                $db->query("UPDATE vass_users SET email = '" . trim($email) . "' WHERE user_id = '" . $member_id ['user_id'] . "'");

                $member_id ['email'] = trim($email);
            }

            $buffer = array("status_code" => 200, "status_text" => "OK", "user" => array("email" => $member_id ['email']));
        } elseif ($action == "services") {

            if (isset($_REQUEST ['force']) && $_REQUEST ['force'] == "remove") {

                if (isset($_REQUEST ['social']))
                    $social = $_REQUEST ['social'];

                if ($social == "facebook") {
                    $buffer ['status_code'] = 200;
                    $buffer ['removed'] = "facebook";
                    $buffer ['status_text'] = "OK";
                    $db->query("DELETE FROM vass_facebook WHERE user_id = '" . $member_id['user_id'] . "'");
                } elseif ($social == "twitter") {
                    $db->query("DELETE FROM vass_twitter WHERE user_id = '" . $member_id['user_id'] . "'");
                    $buffer ['status_code'] = 200;
                    $buffer ['removed'] = "twitter";
                    $buffer ['status_text'] = "OK";
                }
            } else {

                $row = $db->super_query("SELECT screen_id AS twitter_screen_id, screen_name AS twitter_screen_name, date AS twitter_date FROM vass_twitter WHERE user_id = '" . $member_id ['user_id'] . "'");

                if ($row ['twitter_screen_id']) {
                    $service ['twitter'] ['name'] = $row ['twitter_screen_name'];
                    $service ['twitter'] ['last_refresh'] = date('D M d Y H:i:s O', strtotime($row ['twitter_date']));
                    $service ['twitter'] ['pic'] = "http://api.twitter.com/1/users/profile_image?screen_name=" . $row ['twitter_screen_name'] . "&size=bigger";
                    $service ['twitter'] ['lookup_id'] = $row ['twitter_screen_name'];
                    $service ['twitter'] ['type'] = "twitter";
                    $service ['twitter'] ['added_on'] = date('D M d Y H:i:s O', strtotime($row ['twitter_date']));
                }

                $row = $db->super_query("SELECT screen_id AS facebook_screen_id, screen_name AS facebook_screen_name, date AS facebook_date FROM vass_facebook WHERE user_id = '" . $member_id ['user_id'] . "'");

                if ($row ['facebook_screen_id']) {

                    $service ['facebook'] ['name'] = $row ['facebook_screen_name'];
                    $service ['facebook'] ['last_refresh'] = date('D M d Y H:i:s O', strtotime($row ['facebook_date']));
                    $service ['facebook'] ['pic'] = "https://graph.facebook.com/" . $row ['facebook_screen_id'] . "/picture?type=large";
                    $service ['facebook'] ['lookup_id'] = $row ['facebook_screen_id'];
                    $service ['facebook'] ['type'] = "facebook";
                    $service ['facebook'] ['added_on'] = date('D M d Y H:i:s O', strtotime($row ['facebook_date']));
                }

                if (!$service)
                    $service = "";

                $buffer = array("status_code" => 200, "status_text" => "OK", "services" => $service);
            }
        } elseif ($action == "password") {

            $password = $db->safesql(md5($_REQUEST ['password']));

            $new_password = $db->safesql(md5($_REQUEST ['new_password']));

            $confirm_new_password = $db->safesql(md5($_REQUEST ['confirm_new_password']));

            $row = $db->super_query("SELECT user_id FROM vass_users WHERE user_id = '" . $member_id ['user_id'] . "' AND password = '" . $password . "'");

            if (!$row ['user_id']) {
                $buffer = array("status_code" => "400", "status_text" => "Old password is incorrect");
            } else {

                $db->query("UPDATE vass_users SET password = '$new_password' WHERE user_id = '" . $member_id ['user_id'] . "'");

                $buffer = array("status_code" => 200, "status_text" => "OK", "success" => true);
            }
        } elseif ($action == "profile") {

            $bio = $db->safesql($_REQUEST ['bio']);

            $location = $db->safesql($_REQUEST ['location']);

            $name = $db->safesql($_REQUEST ['name']);

            $website = $db->safesql($_REQUEST ['website']);

            $db->query("UPDATE vass_users SET bio = '$bio', location = '$location', name = '$name', website = '$website' WHERE user_id = '" . $member_id ['user_id'] . "'");

            $row = $db->super_query("SELECT vass_users.name, vass_users.bio, vass_users.website, vass_users.total_loved, vass_users.location, vass_users.total_loved, vass_users.total_following, vass_users.total_followers, vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_users LEFT JOIN vass_background ON vass_users.user_id = vass_background.user_id WHERE vass_users.username = '" . $member_id ['username'] . "';");

            $buffer ['status_code'] = 200;

            $buffer ['status_text'] = "OK";

            $buffer ['user'] = $row;
            $buffer ['user'] ['is_beta_tester'] = false;
            $buffer ['user'] ['viewer_following'] = viewer_following($member_id ['user_id']);
            $buffer ['user'] ['import_feeds'] = import_feeds($member_id ['user_id']);
            $buffer ['user'] ['image'] = avatar($member_id ['avatar'], $member_id ['username']);

            $row = $db->super_query("SELECT vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_background WHERE vass_background.user_id = '" . $member_id ['user_id'] . "';");

            if ($row ['image']) {
                $use_image = true;
                $is_default = false;
            } else {
                $is_default = true;
                $use_image = false;
            }

            $buffer ['user'] ['background'] = $row;
            $buffer ['user'] ['background'] ['is_default'] = $is_default;
            $buffer ['user'] ['background'] ['use_image'] = $use_image;

            unset($buffer ['user'] ['password']);
        }
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }

    $db->close();

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "playlistlist") {
    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $page = intval($_REQUEST['page']);

        if (!empty($page) && $page != 0) {
            $letter = $db->safesql($_REQUEST ['letter']);
            $string = $db->safesql($_REQUEST ['string']);

            if (!empty($letter)) {

                $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_playlists WHERE name LIKE '$letter%'  and ( user_id = '" . $user_id . "' or user_access = 1 )");
                $total_results = $total_results['count'];
            } elseif (!empty($string)) {
                $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_playlists WHERE name LIKE '%$string%'  and ( user_id = '" . $user_id . "' or user_access = 1 )");
                $total_results = $total_results['count'];
            } else {

                $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_playlists  WHERE user_id = '" . $user_id . "' or user_access = 1");
                $total_results = $total_results['count'];
            }

            $limit = 20;
            $newpage = $page * $limit;

            if ($page > 1 && $newpage <= $total_results) {
                $previous = $page - 1;
            }
            if ($newpage < $total_results) {
                $next = $page + 1;
            }
            if ($page > 1) {
                $page = ($page - 1) * $limit;
            } else {
                $page = 0;
            }


            if (!empty($letter)) {
                $playlists = $db->query("SELECT id, name FROM vass_playlists WHERE name LIKE '$letter%' and ( user_id = '" . $user_id . "' or user_access = 1 ) LIMIT $limit OFFSET $page");
            } else if (!empty($string)) {
                $playlists = $db->query("SELECT id, name FROM vass_playlists WHERE name LIKE '%$string%' and ( user_id = '" . $user_id . "' or user_access = 1 ) LIMIT $limit OFFSET $page");
            } else {
                $playlists = $db->query("SELECT id, name FROM vass_playlists WHERE user_id = '" . $user_id . "' or user_access = 1 LIMIT $limit OFFSET $page");
            }
            while ($row = $db->get_row($playlists)) {

                $num_songs = $db->super_query("SELECT COUNT(*) AS count FROM vass_song_playlist WHERE playlist_id = '" . $row['id'] . "'");

                $row['total_songs'] = $num_songs['count'];

                $buffer[] = $row;
            }

            if (!$buffer)
                $buffer = array();

            $buffer = array("status_code" => 200, "status_text" => "OK", "results" => 20, "playlists" => $buffer, "total" => $total_results, "next" => $next, "previous" => $previous);
        }else {
            $buffer ['status_code'] = 402;

            $buffer ['status_text'] = "Page parameter wrong ...";
        }
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "playlist") {

    $dir = realpath(ROOT_DIR . "/../public");

    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $action = $db->safesql($_REQUEST ['action']);

        if ($action == "create") {

            $name = $db->safesql($_REQUEST ['name']);

            $descr = $db->safesql($_REQUEST ['descr']);

            $access = $db->safesql($_REQUEST ['access']);
            if (!$name) {
                $buffer ['status_text'] = "Name should not empty";
                $buffer ['status_code'] = 100;
            } else {
                $db->query("INSERT INTO vass_playlists (user_id, `date`, name, descr, user_access) VALUES ('" . $user_id . "', '$_TIME', '$name', '$descr', '$access');");

                $playlist_id = $db->insert_id();


                if (isset($_FILES["file"]) && is_uploaded_file($_FILES["file"]["tmp_name"]) && $_FILES["file"]["error"] == 0) {

                    $id = $playlist_id;
                    $allowedExts = array("jpg", "jpeg", "gif", "png");
                    $image = $_FILES["file"]["tmp_name"];
                    $image_name = $_FILES["file"]['name'];
                    $image_size = $_FILES["file"]['size'];
                    $image_name = str_replace(" ", "_", $image_name);
                    $img_name_arr = explode(".", $image_name);
                    $type = end($img_name_arr);

                    require_once INCLUDE_DIR . '/class/_class_thumb.php';

                    $randnumber = md5(rand(100000, 900000));

                    $res = @move_uploaded_file($image, $dir . "/static/playlists/" . $randnumber);

                    if ($res) {

                        $thumb = new thumbnail($dir . "/static/playlists/" . $randnumber);
                        $thumb->crop('500', '500');
                        $thumb->jpeg_quality(90);
                        $thumb->save($dir . "/static/playlists/" . $id . "_extralarge.jpg");

                        $thumb = new thumbnail($dir . "/static/playlists/" . $randnumber);
                        $thumb->crop('250', '250');
                        $thumb->jpeg_quality(90);
                        $thumb->save($dir . "/static/playlists/" . $id . "_large.jpg");

                        $thumb = new thumbnail($dir . "/static/playlists/" . $randnumber);
                        $thumb->crop('120', '120');
                        $thumb->jpeg_quality(90);
                        $thumb->save($dir . "/static/playlists/" . $id . "_medium.jpg");

                        $thumb = new thumbnail($dir . "/static/playlists/" . $randnumber);
                        $thumb->crop('75', '75');
                        $thumb->jpeg_quality(90);
                        $thumb->save($dir . "/static/playlists/" . $id . "_small.jpg");

                        @unlink($dir . "/static/playlists/" . $randnumber);

                        $row = $db->query("UPDATE vass_playlists SET `cover` = 1 WHERE `user_id` = '" . $user_id . "' AND id = '$id';");
                        $buffer ['playlist']['cover'] = 1;
                        $buffer ['playlist']['cover_uploadingtext'] = "success";
                    } else {
                        $buffer ['playlist']['cover'] = 0;
                        $buffer ['playlist']['cover_uploadingtext'] = "error uploading image.....";
                    }
                }




                $buffer ['status_text'] = "OK";
                $buffer ['status_code'] = 200;
                $buffer ['playlist']['name'] = $name;
                $buffer ['playlist']['date'] = $_TIME;
                $buffer ['playlist']['playlist_id'] = $playlist_id;

                $buffer ['playlist']['descr'] = $descr;
            }
        } elseif ($action == "edit") {

            $name = $db->safesql($_REQUEST ['name']);

            $descr = $db->safesql($_REQUEST ['descr']);

            $access = $db->safesql($_REQUEST ['access']);

            $id = intval($_REQUEST ['id']);

            if (!$name) {
                $buffer ['status_text'] = "Name should not empty";
                $buffer ['status_code'] = 100;
            } else {

                $db->query("UPDATE vass_playlists SET name= '$name', descr = '$descr', user_access = '$access' WHERE user_id = '" . $member_id ['user_id'] . "' AND id = '$id';");

                if (isset($_FILES["file"]) && is_uploaded_file($_FILES["file"]["tmp_name"]) && $_FILES["file"]["error"] == 0) {


                    $allowedExts = array("jpg", "jpeg", "gif", "png");
                    $image = $_FILES["file"]["tmp_name"];
                    $image_name = $_FILES["file"]['name'];
                    $image_size = $_FILES["file"]['size'];
                    $image_name = str_replace(" ", "_", $image_name);
                    $img_name_arr = explode(".", $image_name);
                    $type = end($img_name_arr);

                    require_once INCLUDE_DIR . '/class/_class_thumb.php';

                    $randnumber = md5(rand(100000, 900000));

                    $res = @move_uploaded_file($image, $dir . "/static/playlists/" . $randnumber);

                    if ($res) {

                        $thumb = new thumbnail($dir . "/static/playlists/" . $randnumber);
                        $thumb->crop('500', '500');
                        $thumb->jpeg_quality(90);
                        $thumb->save($dir . "/static/playlists/" . $id . "_extralarge.jpg");

                        $thumb = new thumbnail($dir . "/static/playlists/" . $randnumber);
                        $thumb->crop('250', '250');
                        $thumb->jpeg_quality(90);
                        $thumb->save($dir . "/static/playlists/" . $id . "_large.jpg");

                        $thumb = new thumbnail($dir . "/static/playlists/" . $randnumber);
                        $thumb->crop('120', '120');
                        $thumb->jpeg_quality(90);
                        $thumb->save($dir . "/static/playlists/" . $id . "_medium.jpg");

                        $thumb = new thumbnail($dir . "/static/playlists/" . $randnumber);
                        $thumb->crop('75', '75');
                        $thumb->jpeg_quality(90);
                        $thumb->save($dir . "/static/playlists/" . $id . "_small.jpg");

                        @unlink($dir . "/static/playlists/" . $randnumber);

                        $row = $db->query("UPDATE vass_playlists SET `cover` = 1 WHERE `user_id` = '" . $user_id . "' AND id = '$id';");

                        $buffer ['cover_uploadingtext'] = "success";
                    } else {

                        $buffer ['cover_uploadingtext'] = "error uploading....Try again.";
                    }
                }
                $buffer ['name'] = $name;
                $buffer ['descr'] = $descr;
                $buffer ['status_text'] = "OK";
                $buffer ['status_code'] = 200;
            }
        } elseif ($action == "doresort") {


            $playlist_id = intval($_REQUEST ['playlist_id']);

            $items = $_REQUEST ['songs'];

            $items = explode("==", $items);

            $i = 1;

            foreach ($items as $item) {

                if (!empty($item)) {
                    $db->query("UPDATE vass_song_playlist SET pos = '$i' WHERE song_id = '" . $db->safesql($item) . "' AND playlist_id = '$playlist_id'");
                    $i++;
                }
                $buffer ['status_text'] = "OK";
                $buffer ['status_code'] = 200;
            }
        } elseif ($action == "remove") {

            $id = intval($_REQUEST ['id']);


            if ($id) {
                $db->query("DELETE FROM vass_playlists WHERE user_id = '" . $user_id . "' AND id = '$id';");
                $buffer ['playlist_id'] = $id;
                $buffer ['status_text'] = "OK";
                $buffer ['status_code'] = 200;
            } else {
                $buffer ['status_text'] = "id required";
                $buffer ['status_code'] = 100;
            }
        } elseif ($action == "info") {

            $id = intval($_REQUEST ['id']);

            $row = $db->super_query("SELECT vass_playlists.name, vass_playlists.date, vass_playlists.id AS playlist_id, vass_playlists.cover, vass_playlists.descr,
		vass_users.username 
		FROM vass_playlists LEFT JOIN vass_users ON vass_playlists.user_id = vass_users.user_id WHERE vass_playlists.id = '" . $id . "';");

            $playlist = $row;

            $buffer = array("status_code" => 200, "status_text" => "OK", "playlist" => $playlist);
        } elseif ($action == "addsong") {

            $playlist_id = intval($_REQUEST ['playlist_id']);

            $song_id = $db->safesql($_REQUEST ['song_id']);

            if ($song_id && $playlist_id)
                $db->query("INSERT IGNORE INTO vass_song_playlist (song_id, playlist_id) VALUES ('$song_id', '$playlist_id')");

            $buffer = array("status_code" => 200, "status_text" => "OK");
        }elseif ($action == "addfromqueue") {

            $playlist_id = intval($_REQUEST ['playlist_id']);

            $json = file_get_contents('php://input');

            $obj = json_decode($json);

            foreach ($obj as $item) {

                $song_id = $item->id;

                if ($song_id && $playlist_id)
                    $db->query("INSERT IGNORE INTO vass_song_playlist (song_id, playlist_id) VALUES ('$song_id', '$playlist_id')");
            }

            $buffer = array("status_code" => 200, "status_text" => "OK");
        }elseif ($action == "remove_song") {


            $playlist_id = intval($_REQUEST ['playlist_id']);

            $song_id = $db->safesql($_REQUEST ['song_id']);

            if ($song_id && $playlist_id)
                $db->query("DELETE FROM vass_song_playlist WHERE song_id = '$song_id' AND playlist_id = '$playlist_id'");

            $buffer = array("status_code" => 200, "status_text" => "OK");
        }elseif ($action == "songs") {

            $playlist_id = intval($_REQUEST ['id']);

            $owner = $db->super_query("SELECT user_id FROM vass_playlists WHERE id = '$playlist_id'");

            $sql_query = $db->query("SELECT vass_songs.artist_id, vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
				vass_artists.id AS artist_id, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id 
				FROM vass_song_playlist LEFT JOIN vass_songs ON vass_song_playlist.song_id = vass_songs.id LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN 
				vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_song_playlist.playlist_id = '" . $playlist_id . "' ORDER BY vass_song_playlist.pos ASC LIMIT $start,20");

            $total_results = $db->super_query("SELECT COUNT(*) AS count 
				FROM vass_song_playlist LEFT JOIN vass_songs ON vass_song_playlist.song_id = vass_songs.id LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN 
				vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_song_playlist.playlist_id = '" . $playlist_id . "'");

            while ($row = $db->get_row($sql_query)) {

                if ($logged && $member_id['user_id'] == $owner ['user_id']) {
                    $songs['playlist_owner'] = true;
                    $songs['playlist_id'] = $playlist_id;
                }
                if ($row['artist_id']) {

                    $songs ['album'] = $row ['song_album'];
//                $songs ['url'] = stream($row ['song_id']);
                    $songs ['image'] = songlist_images($row ['album_id'], $row['artist_id']);
                    $songs ['artist'] = $row ['song_artist'];
                } else {

                    $songs ['album'] = $row ['description'];
//                $songs ['url'] = stream($row ['song_id']);
                    $songs ['image'] = songlist_images($row ['artwork_url'], $row['artist_id']);
                    $songs ['artist'] = $row ['tag_list'];
                }
                $songs ['artist_id'] = $row ['artist_id'];
                $songs ['similar_artists'] = similar_artists($row ['song_id']);
                $songs ['buy_link'] = null;
                $songs ['title'] = $row ['song_title'];
                $songs ['metadata_state'] = metadata_state($row ['song_id']);
                $songs ['sources'] = sources($row ['song_id']);
                $songs ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                $songs ['last_loved'] = null;
                $songs ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                $songs ['aliases'] = aliases($row ['song_id']);
                $songs ['loved_count'] = $row ['loved'];
                $songs ['id'] = $row ['song_id'];
                $songs ['tags'] = tags($row ['song_id']);
                $songs ['trending_rank_today'] = trending_rank_today($row ['song_id']);
                $songs ['user_love'] = null;
                $result ['songs'] [] = $songs;
            }

            $result ['status_text'] = "OK";
            $result ['status_code'] = "200";
            $result ['results'] = $total_results['count'];
            $result ['start'] = $start;
            $result ['total'] = $total_results['count'];

            $buffer = $result;
        } elseif ($action == "resort") {

            $playlist_id = intval($_REQUEST ['id']);

            $owner = $db->super_query("SELECT user_id FROM vass_playlists WHERE id = '$playlist_id'");

            $sql_query = $db->query("SELECT vass_songs.artist_id, vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
				vass_artists.id AS artist_id, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id 
				FROM vass_song_playlist LEFT JOIN vass_songs ON vass_song_playlist.song_id = vass_songs.id LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN 
				vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_song_playlist.playlist_id = '" . $playlist_id . "' ORDER BY vass_song_playlist.pos ASC");

            $total_results = $db->super_query("SELECT COUNT(*) AS count 
				FROM vass_song_playlist LEFT JOIN vass_songs ON vass_song_playlist.song_id = vass_songs.id LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN 
				vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_song_playlist.playlist_id = '" . $playlist_id . "'");
            $i = 0;
            while ($row = $db->get_row($sql_query)) {

                if ($row['artist_id']) {

                    $songs ['album'] = $row ['song_album'];
//                $songs ['url'] = stream($row ['song_id']);
                    $songs ['image'] = songlist_images($row ['album_id'], $row['artist_id']);
                    $songs ['artist'] = $row ['song_artist'];
                } else {

                    $songs ['album'] = $row ['description'];
//                $songs ['url'] = stream($row ['song_id']);
                    $songs ['image'] = songlist_images($row ['artwork_url'], $row['artist_id']);
                    $songs ['artist'] = $row ['tag_list'];
                }
                $songs ['artist_id'] = $row ['artist_id'];
                $songs ['similar_artists'] = similar_artists($row ['song_id']);
                $songs ['buy_link'] = null;
                $songs ['title'] = $row ['song_title'];
                $songs ['metadata_state'] = metadata_state($row ['song_id']);
                $songs ['sources'] = sources($row ['song_id']);
                $songs ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                $songs ['last_loved'] = null;
                $songs ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                $songs ['aliases'] = aliases($row ['song_id']);
                $songs ['loved_count'] = $row ['loved'];
                $songs ['id'] = $row ['song_id'];
                $songs ['tags'] = tags($row ['song_id']);
                $songs ['trending_rank_today'] = trending_rank_today($row ['song_id']);
                $songs ['user_love'] = null;
                $songs ['position'] = $i;
                $songs['playlist_id'] = $playlist_id;
                $buffer [] = $songs;
                $i++;
            }
        }
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "last_playlists") {

    $sql_result = $db->query("SELECT vass_playlists.name, vass_playlists.date, vass_playlists.id AS playlist_id, vass_playlists.cover, vass_playlists.descr,
	vass_users.username 
	FROM vass_playlists LEFT JOIN vass_users ON vass_playlists.user_id = vass_users.user_id ORDER by vass_playlists.id DESC;");

    $start = $_REQUEST ['start'];

    $page_start = $start;

    $page_end = $start + 20;

    $total_results = $db->num_rows($sql_result);

    $i = 0;

    while ($row = $db->get_row($sql_result)) {

        if ($i >= $page_start) {

            $object ['title'] = $row ['name'];
            $object ['object'] ['title'] = $row ['name'];
            $object ['object'] ['album'] = $row ['description'];
//            $object ['object'] ['url'] = stream($row ['song_id']);
            $object ['object'] ['artist'] = $row ['username'];
            $object ['object'] ['playlist_id'] = $row ['playlist_id'];
            $object ['object'] ['cover'] = $row ['cover'];
            $object ['object'] ['descr'] = $row ['descr'];
            $object ['object'] ['created_on'] = date('D M d Y H:i:s O', strtotime($row ['date']));
            $object ['object'] ['owner'] = array("username" => $row ['username'], "created_on" => date('D M d Y H:i:s O', strtotime($row ['created_on'])));
            $activities [] = $object;
        }
        $i ++;
        if ($i >= $page_end)
            break;
    }


    $buffer ['status_text'] = "OK";
    $buffer ['status_code'] = "200";
    $buffer ['results'] = $total_results;
    $buffer ['start'] = $start;
    $buffer ['total'] = $total_results;
    $buffer ['activities'] = $activities;

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
}elseif ($type == "artist") {
    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {
        $action = $db->safesql($_REQUEST ['action']);

        if ($action == "info") {

            $id = intval($_REQUEST ['id']);

            $row = $db->super_query("SELECT id AS artist_id, name, bio FROM vass_artists WHERE id = '" . $id . "';");

            $artist = $row;
            $artist = $row;
            $rowuser1 = explode(",", $row['likecount']);
//         echo  $_SESSION['user_id'];
            $upd = 0;
            for ($i = 0; $i < count($rowuser1); $i++) {
                if ($rowuser1[$i] == $_SESSION['user_id']) {
                    $upd = 1;
                    break;
                }
            }
            $artist['upd'] = $upd;

            $buffer = array("status_code" => 200, "status_text" => "OK", "artist" => $artist);
        } else {

            $response = new stdClass();

            $id = intval($_REQUEST ['id']);
            $D_SORT = 800;

            $sql_query = $db->query("SELECT COUNT(*) AS count, song_id FROM vass_analz WHERE `time` > '" . date("Y-m-d", (time() - $D_SORT * 24 * 3600)) . "' and artist_id = '" . $id . "' GROUP BY song_id ORDER by count DESC ");
//            echo "<pre>";
//            print_r($sql_query);
//            echo"<pre>";
//            die;
            $i = 0;
            while ($top = $db->get_row($sql_query)) {

                if ($top ['song_id']) {
                    $i++;
                    $row = $db->super_query("SELECT vass_songs.artist_id, vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
			vass_artists.id AS artist_id, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id 
			FROM vass_songs LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN 
			vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_songs.id = '" . $top ['song_id'] . "'");
                    $songs ['album'] = $row ['song_album'];
                    $songs ['artist_id'] = $row ['artist_id'];
                    $songs ['similar_artists'] = similar_artists($row ['song_id']);
                    $songs ['buy_link'] = null;
                    $songs ['artist'] = $row ['song_artist'];
//                    $songs ['url'] = stream($row ['song_id']);
                    $songs ['image'] = songlist_images($row ['album_id']);
                    $songs ['title'] = $row ['song_title'];
                    $songs ['metadata_state'] = metadata_state($row ['song_id']);
                    $songs ['sources'] = sources($row ['song_id']);
                    $songs ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                    $songs ['last_loved'] = null;
                    $songs ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                    $songs ['aliases'] = aliases($row ['song_id']);
                    $songs ['loved_count'] = $row ['loved'];
                    $songs ['id'] = $row ['song_id'];
                    $songs ['tags'] = tags($row ['song_id']);
                    $songs ['trending_rank_today'] = trending_rank_today($row ['song_id']);
                    $songs ['user_love'] = "";
                    $songlist[] = $songs;
                }
            }

//            print_r($songlist);
//            echo"<pre>";
//            die;
            $response->songs = $songlist;
            $response->songstotal = $i;
//             $result['songs'] = (array)$songlist;         
//            $result ['songs']['total'] = $i;
            //artistbio
            $db->query("SELECT bio FROM vass_artists WHERE id = '$id'");

            while ($row = $db->get_row()) {

                $bio[] = $row;
            }
            $response->bio = $bio;

            // artistsimilar

            $db->query("SELECT tag FROM vass_artists WHERE id = '$id'");


            $tags = $db->get_row();
            $tags = explode(",", $tags[tag]);
//          echo count($tags);

            for ($i = 0; $i < count($tags); $i++) {
                $db->query("SELECT id FROM vass_artists WHERE vass_artists.tag REGEXP '[[:<:]]" . $tags[$i] . "[[:>:]]' and vass_artists.id <> '$id' ");

                while ($artist = $db->get_row()) {
                    if ($artist['id'])
                        $artists_id[] = $artist['id'];
                }
            }
            if ($artists_id) {
                $artists_id = array_unique($artists_id);
            }
//        echo"<pre>";print_r($artists_id);echo "<pre>";die;
            for ($j = 0; $j < count($artists_id); $j++) {
//             echo"<pre>";print_r($artists_id[]);echo "<pre>";die;
                $db->query("SELECT id ,name FROM vass_artists WHERE id='" . $artists_id[$j] . "'");

                while ($Row = $db->get_row()) {
                    if ($Row)
                        $similarartist[] = $Row;
                }
            }

            $total_results = count($artists_id);
            $response->similar_artist = $similarartist;
            $response->similar_artisttotal = $total_results;


            //artistallalbum

            $db->query("SELECT vass_albums.artist_id, vass_artists.name AS artist, vass_albums.id, vass_albums.id, vass_albums.view, vass_albums.name 
	FROM vass_albums LEFT JOIN vass_artists ON vass_albums.artist_id = vass_artists.id WHERE vass_artists.id = '$id'");

            while ($row = $db->get_row()) {

                $albums[] = $row;
            }



            $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_albums WHERE artist_id = '$id'");
            $total_results = $total_results['count'];
            $response->albums = $albums;
            $response->albumstotal = $total_results;

            $response->status_text = "OK";
            $response->status_code = 200;




            $buffer = $response;
        }
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');
    // header('Content-type: application/text');
//print_r($buffer);


    echo json_encode($buffer, true);
} elseif ($type == "artistbio") {
    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {


        $id = intval($_REQUEST ['id']);

        $db->query("SELECT bio FROM vass_artists WHERE id = '$id'");

        while ($row = $db->get_row()) {

            $buffer[] = $row;
        }

        if (!$buffer)
            $buffer = array();



        $buffer = array("status_code" => 200, "status_text" => "OK", "buffer" => $buffer);
    }else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "artistsimilar") {
    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $id = intval($_REQUEST ['id']);

        $db->query("SELECT tag FROM vass_artists WHERE id = '$id'");


        $tags = $db->get_row();
        $tags = explode(",", $tags[tag]);
//          echo count($tags);

        for ($i = 0; $i < count($tags); $i++) {
            $db->query("SELECT id FROM vass_artists WHERE vass_artists.tag REGEXP '[[:<:]]" . $tags[$i] . "[[:>:]]' and vass_artists.id <> '$id' ");

            while ($artist = $db->get_row()) {
                if ($artist['id'])
                    $artists_id[] = $artist['id'];
            }
        }
        if ($artists_id) {
            $artists_id = array_unique($artists_id);
        }
//        echo"<pre>";print_r($artists_id);echo "<pre>";die;
        for ($j = 0; $j < count($artists_id); $j++) {
//             echo"<pre>";print_r($artists_id[]);echo "<pre>";die;
            $db->query("SELECT id ,name FROM vass_artists WHERE id='" . $artists_id[$j] . "'");

            while ($Row = $db->get_row()) {
                if ($Row)
                    $buffer[] = $Row;
            }
        }

        $total_results = count($artists_id);
        if (!$buffer)
            $buffer = array();

        $buffer = array("status_code" => 200, "status_text" => "OK", "buffer" => $buffer, "total" => $total_results);
    }else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "album") {

//    echo"<pre>";
//    print_r($_REQUEST);
//    echo"<pre>";
//   die;
    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {
//    echo"<pre>";
//    print_r($_REQUEST);
//    echo"<pre>";
//   die;
        $action = $db->safesql($_REQUEST ['action']);

        if ($action == "info") {

            $id = intval($_REQUEST ['id']);

            $row = $db->super_query("SELECT vass_albums.descr, vass_albums.id AS album_id, vass_albums.name, vass_artists.id AS artist_id, vass_artists.name AS artist FROM vass_albums LEFT JOIN vass_artists ON vass_albums.artist_id =  vass_artists.id WHERE vass_albums.id = '" . $id . "';");

            $album = $row;

            $buffer = array("status_code" => 200, "status_text" => "OK", "album" => $album);
        } elseif ($action == "song") {

            $id = intval($_REQUEST ['id']);

            $sql_query = $db->query("SELECT vass_songs.artist_id, vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
				vass_artists.id AS artist_id, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id 
				FROM vass_songs LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN 
				vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_songs.album_id = '$id' ");

            $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_songs WHERE album_id = '$id'");

            while ($row = $db->get_row($sql_query)) {

                $songs ['album'] = $row ['song_album'];
//                $songs ['url'] = stream($row ['song_id']);
                $songs ['image'] = songlist_images($row ['album_id'], $row['artist_id']);
                $songs ['artist'] = $row ['song_artist'];
                $songs ['artist_id'] = $row ['artist_id'];
                $songs ['similar_artists'] = similar_artists($row ['song_id']);
                $songs ['buy_link'] = null;
                $songs ['title'] = $row ['song_title'];
                $songs ['metadata_state'] = metadata_state($row ['song_id']);
                $songs ['sources'] = sources($row ['song_id']);
                $songs ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                $songs ['last_loved'] = null;
                $songs ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                $songs ['aliases'] = aliases($row ['song_id']);
                $songs ['loved_count'] = $row ['loved'];
                $songs ['id'] = $row ['song_id'];
                $songs ['tags'] = tags($row ['song_id']);
                $songs ['trending_rank_today'] = trending_rank_today($row ['song_id']);
                $songs ['user_love'] = null;
                $result ['songs'] [] = $songs;
            }

            $result ['status_text'] = "OK";
            $result ['status_code'] = "200";
            $result ['results'] = $total_results['count'];
            $result ['start'] = $start;
            $result ['total'] = $total_results['count'];

            $buffer = $result;
        }
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "userlist") {

    $start = $_REQUEST['start'];

    $sql_result = $db->query("SELECT vass_friendship.follower_id, vass_users.username, vass_users.name, vass_users.bio, vass_users.website, vass_users.total_loved, vass_users.location, vass_users.total_loved, vass_users.total_following, vass_users.total_followers, vass_users.avatar, vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_users LEFT JOIN vass_background ON vass_users.user_id = vass_background.user_id LEFT JOIN vass_friendship ON vass_users.user_id = vass_friendship.follower_id LIMIT 0,50");

    $total_results = $db->num_rows($sql_result);

    while ($result = $db->get_row($sql_result)) {
        $buffer = $result;
        $buffer['is_beta_tester'] = false;
        $buffer['viewer_following'] = viewer_following($result['follower_id']);
        $buffer['import_feeds'] = import_feeds($result['user_id']);
        $buffer['image'] = avatar($result['avatar'], $result['username']);

        $row = $db->super_query("SELECT vass_background.color, vass_background.image, vass_background.position, vass_background.repeat, vass_background.use_image FROM vass_background WHERE vass_background.user_id = '" . $result['user_id'] . "';");

        if ($row['image']) {
            $use_image = true;
            $is_default = false;
        } else {
            $is_default = true;
            $use_image = false;
        }
        $buffer['background'] = $row;
        $buffer['background']['is_default'] = $is_default;
        $buffer['background']['use_image'] = $use_image;

        unset($buffer['password']);

        $following[] = $buffer;
    }


    $buffer = array("status_code" => 200, "status_text" => "OK", "results" => 20, "start" => $start, "users" => $following, "total" => $total_results);

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "albumlist") {


    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");


    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $letter = $db->safesql($_REQUEST ['letter']);

        $page = intval($_REQUEST['page']);

        $string = $db->safesql($_REQUEST ['string']);



        if (!empty($letter)) {

            $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_albums WHERE vass_albums.name LIKE '$letter%'");
            $total_results = $total_results['count'];
        } else if (!empty($string)) {

            $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_albums WHERE vass_albums.name LIKE '%$string%'");
            $total_results = $total_results['count'];
        } else {

            $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_albums");
            $total_results = $total_results['count'];
        }
        $limit = 20;
        $newpage = $page * $limit;

        if ($page > 1 && $newpage <= $total_results) {
            $previous = $page - 1;
        }
        if ($newpage < $total_results) {
            $next = $page + 1;
        }
        if ($page > 1) {
            $page = ($page - 1) * $limit;
        } else {
            $page = 0;
        }


        if (!empty($letter)) {

            $query = $db->query("SELECT vass_albums.artist_id, vass_artists.name AS artist, vass_albums.id, vass_albums.id, vass_albums.view, vass_albums.name FROM vass_albums LEFT JOIN vass_artists ON vass_albums.artist_id = vass_artists.id WHERE vass_albums.name LIKE '$letter%' LIMIT $limit OFFSET $page");
        } else if (!empty($string)) {

            $query = $db->query("SELECT vass_albums.artist_id, vass_artists.name AS artist, vass_albums.id, vass_albums.id, vass_albums.view, vass_albums.name FROM vass_albums LEFT JOIN vass_artists ON vass_albums.artist_id = vass_artists.id WHERE vass_albums.name LIKE '%$string%' LIMIT $limit OFFSET $page");
        } else {

            $query = $db->query("SELECT vass_albums.artist_id, vass_artists.name AS artist, vass_albums.id, vass_albums.id, vass_albums.view, vass_albums.name FROM vass_albums LEFT JOIN vass_artists ON vass_albums.artist_id = vass_artists.id LIMIT $limit OFFSET $page");
        }

        while ($row = $db->get_row($query)) {

            $buffer[] = $row;
        }

        if (!$buffer)
            $buffer = array();

        $buffer = array("status_code" => 200, "status_text" => "OK", "results" => 1000, "albums" => $buffer, "total" => $total_results, 'next' => $next, 'previous' => $previous);
    }else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "artistlist") {

    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");


    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {


        $letter = $db->safesql($_REQUEST ['letter']);

        $page = intval($_REQUEST['page']);

        $string = $db->safesql($_REQUEST ['string']);


        if (!empty($letter)) {

            $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_artists WHERE name LIKE '$letter%'");
            $total_results = $total_results['count'];
        } else if (!empty($string)) {

            $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_artists WHERE name LIKE '%$string%'");
            $total_results = $total_results['count'];
        } else {

            $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_artists");
            $total_results = $total_results['count'];
        }


        $limit = 20;
        $newpage = $page * $limit;

        if ($page > 1 && $newpage <= $total_results) {
            $previous = $page - 1;
        }
        if ($newpage < $total_results) {
            $next = $page + 1;
        }
        if ($page > 1) {
            $page = ($page - 1) * $limit;
        } else {
            $page = 0;
        }


        if (!empty($letter)) {


            $artists = $db->query("SELECT id, name FROM vass_artists WHERE name LIKE '$letter%' LIMIT $limit OFFSET $page ");
            // $artists = $db->query("SELECT id, name FROM vass_artists WHERE name LIKE '$letter%' LIMIT $limit OFFSET $page  order by name");
        } else if (!empty($string)) {

            $artists = $db->query("SELECT id, name FROM vass_artists WHERE name LIKE '%$string%' LIMIT $limit OFFSET $page ");
            // $artists = $db->query("SELECT id, name FROM vass_artists WHERE name LIKE '%$string%' LIMIT $limit OFFSET $page order by name ");
        } else {

            $artists = $db->query("SELECT id, name FROM vass_artists LIMIT $limit OFFSET $page");
        }


        while ($row = $db->get_row($artists)) {

            $num_songs = $db->super_query("SELECT COUNT(*) AS count FROM vass_songs WHERE artist_id = '" . $row['id'] . "'");

            $row['total_songs'] = $num_songs['count'];

            $buffer[] = $row;
        }

        if (!$buffer)
            $buffer = array();

        $buffer = array("status_code" => 200, "status_text" => "OK", "results" => 20, "artists" => $buffer, "total" => $total_results, "next" => $next, "previous" => $previous);
    }else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "suggess") {

    //{"status_text": "OK", "status_code": 200, "suggess": [{"title":"A Men", "url":"artist\/a-men\/1"},{"title":"A Zflow", "url":"artist\/a-zflow\/2"},{"title":"Bari Niich", "url":"album\/bari-niich\/2"},{"title":"Dir Lkhir Talqa Lkhir", "url":"album\/dir-lkhir-talqa-lkhir\/3"},{"title":"Bari niich", "url":"song\/bari-niich\/2"},{"title":"Dir Lkhir Talqa Lkhir", "url":"song\/dir-lkhir-talqa-lkhir\/3"}]}

    $query = $db->safesql($_REQUEST['query']);

    $db->query("SELECT id, title FROM vass_songs WHERE title LIKE '%$query%' LIMIT 0,10");

    while ($row = $db->get_row()) {

        $buffer[] = $row;
    }

    $buffer = array("status_code" => 200, "suggess" => $buffer);

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "searchalbum") {

    $q = $db->safesql($_REQUEST ['q']);

    $db->query("SELECT vass_albums.artist_id, vass_artists.name AS artist, vass_albums.id, vass_albums.id, vass_albums.view, vass_albums.name FROM vass_albums LEFT JOIN vass_artists ON vass_albums.artist_id = vass_artists.id WHERE vass_albums.name LIKE '%$q%' LIMIT 0,20");

    while ($row = $db->get_row()) {

        $buffer[] = $row;
    }

    if (!$buffer)
        $buffer = array();

    $buffer = array("status_code" => 200, "status_text" => "OK", "results" => 20, "start" => $start, "albums" => $buffer, "total" => $total_results);

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
}elseif ($type == "searchartist") {

    $q = $db->safesql($_REQUEST ['q']);

    $db->query("SELECT id, name FROM vass_artists WHERE name LIKE '%$q%' LIMIT 0,5");

    while ($row = $db->get_row()) {

        $buffer[] = $row;
    }

    if (!$buffer)
        $buffer = array();

    $buffer = array("status_code" => 200, "status_text" => "OK", "results" => 20, "start" => $start, "albums" => $buffer, "total" => $total_results);

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
}elseif ($type == "artistallalbum") {


    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");


    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $id = intval($_REQUEST ['id']);

        $db->query("SELECT vass_albums.artist_id, vass_artists.name AS artist, vass_albums.id, vass_albums.id, vass_albums.view, vass_albums.name 
	FROM vass_albums LEFT JOIN vass_artists ON vass_albums.artist_id = vass_artists.id WHERE vass_artists.id = '$id'");

        while ($row = $db->get_row()) {

            $buffer[] = $row;
        }

        if (!$buffer)
            $buffer = array();

        $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_albums WHERE artist_id = '$id'");
        $total_results = $total_results['count'];

        $buffer = array("status_code" => 200, "status_text" => "OK", "albums" => $buffer, "total" => $total_results);
    }else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "artistallvideo") {

    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");


    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {
        $id = intval($_REQUEST ['id']);

        $db->query("SELECT vass_videos.artist_id, vass_videos.tube_key, vass_artists.name AS artist, vass_videos.id, vass_videos.id, vass_videos.view, vass_videos.name 
	FROM vass_videos LEFT JOIN vass_artists ON vass_videos.artist_id = vass_artists.id WHERE vass_artists.id = '$id'");

        while ($row = $db->get_row()) {

            $buffer[] = $row;
        }

        if (!$buffer)
            $buffer = array();

        $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_videos WHERE artist_id = '$id'");

        $total_results = intval($total_results['count']);

        $buffer = array("status_code" => 200, "status_text" => "OK", "results" => 20, "start" => $start, "videos" => $buffer, "total" => $total_results);
    }else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "video") {

    $id = intval($_REQUEST ['id']);

    $row = $db->super_query("SELECT vass_videos.artist_id, vass_videos.tube_key, vass_artists.name AS artist, vass_videos.id, vass_videos.id, vass_videos.view, vass_videos.name 
	FROM vass_videos LEFT JOIN vass_artists ON vass_videos.artist_id = vass_artists.id WHERE vass_videos.id = '$id'");

    if (!$buffer)
        $buffer = array();

    $buffer = array("status_code" => 200, "status_text" => "OK", "results" => 20, "video" => $row);

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
}elseif ($type == "newrelease") {
    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");


    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $page = intval($_REQUEST['page']);

        $total_result = $db->super_query("SELECT count(*) as count FROM vass_songs WHERE vass_songs.recent = 1 ");
        $count = $total_result['count'];

        $limit = 20;
        $newpage = $page * $limit;

        if ($page > 1 || $newpage <= $count) {
            $previous = $page - 1;
        }
        if ($newpage < $count) {
            $next = $page + 1;
        }
        if ($page > 1) {
            $page = ($page - 1) * $limit;
        } else {
            $page = 0;
        }



        $release = $db->query("SELECT vass_songs.id AS song_id, vass_songs.artist_id, 
			vass_songs.id AS song_id, vass_songs.loved, vass_songs.title AS song_title, 
			vass_artists.id AS artist_id, vass_artists.name AS song_artist, vass_albums.name AS song_album, vass_albums.id AS album_id 
			FROM vass_songs LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN 
			vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_songs.recent = 1 LIMIT $limit OFFSET $page");


        while ($row = $db->get_row($release)) {
            if ($row ['song_id']) {
                $songs ['album'] = $row ['song_album'];
                $songs ['artist_id'] = $row ['artist_id'];
                $songs ['similar_artists'] = similar_artists($row ['song_id']);
                $songs ['buy_link'] = null;
                $songs ['artist'] = $row ['song_artist'];
//                $songs ['url'] = stream($row ['song_id']);
                $songs ['image'] = songlist_images($row ['album_id']);
                $songs ['artist_image'] = artist_images($row ['album_id'], $row ['artist_id']);
                $songs ['title'] = $row ['song_title'];
                $songs ['metadata_state'] = metadata_state($row ['song_id']);
                $songs ['sources'] = sources($row ['song_id']);
                $songs ['viewer_love'] = viewer_love($row ['song_id'], $user_id);
                $songs ['last_loved'] = null;
                $songs ['recent_loves'] = recent_loves($row ['song_id'], $user_id);
                $songs ['aliases'] = aliases($row ['song_id']);
                $songs ['loved_count'] = $row ['loved'];
                $songs ['id'] = $row ['song_id'];
                $songs ['tags'] = tags($row ['song_id']);
                $songs ['trending_rank_today'] = $i;
                $result ['songs'] [] = $songs;
            } else
                $result ['songs'] = array();
            $i ++;
        }
        $result['total'] = $count;
        $result ['status_text'] = "OK";
        $result ['status_code'] = "200";
        $result ['next'] = $next;
        $result ['previous'] = $previous;
    }else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    echo json_encode($result);
} elseif ($type == "toplist") {

    $type = $db->safesql(trim($_REQUEST ['type']));

    if ($type == "album")
        $query = $db->query("SELECT vass_albums.artist_id, vass_artists.name AS artist, vass_albums.id, vass_albums.id, vass_albums.view, vass_albums.name FROM vass_albums LEFT JOIN vass_artists ON vass_albums.artist_id = vass_artists.id WHERE vass_albums.name LIKE '$letter%' LIMIT 0,20");
    else if ($type == "artist")
        $query = $db->query("SELECT id, name FROM vass_artists LIMIT 0,20");
    else if ($type == "playlist")
        $query = $db->query("SELECT vass_playlists.name, vass_playlists.date, vass_playlists.id AS playlist_id, vass_playlists.cover, vass_playlists.descr,
		vass_users.username FROM vass_playlists LEFT JOIN vass_users ON vass_playlists.user_id = vass_users.user_id LIMIT 0,20;");

    while ($row = $db->get_row($query)) {
        if ($type == "album")
            $row['type'] = "album";
        else if ($type == "artist") {

            $row['type'] = "artist";
            $num_songs = $db->super_query("SELECT COUNT(*) AS count FROM vass_songs WHERE artist_id = '" . $row['id'] . "'");
            $row['total_songs'] = $num_songs['count'];
        } else if ($type == "playlist")
            $row['type'] = "playlist";

        $buffer[] = $row;
    }

    if (!$buffer)
        $buffer = array();

    $buffer = array("status_code" => 200, "status_text" => "OK", "buffers" => $buffer);

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
}elseif ($type == "lyrics") {

    $song_id = $db->safesql(trim(intval($_REQUEST ['song_id'])));

    $row = $db->super_query("SELECT lyrics FROM vass_songs WHERE id = '$song_id'");

    $row['lyrics'] = stripslashes(str_replace("\n", "<br>", $row['lyrics']));

    $buffer['lyrics'] = $row['lyrics'];

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
} elseif ($type == "payment") {

    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $song_id = $db->safesql(trim(intval($_REQUEST ['song_id'])));
        $transactionid = $db->safesql(trim($_REQUEST ['transactionid']));
        $amount = $db->safesql(trim(intval($_REQUEST ['amt'])));
        $transactiontime = $db->safesql(trim($_REQUEST ['time']));
        $user_id = $db->safesql(trim(intval($_REQUEST ['user_id'])));

        $db->query("INSERT INTO vass_transaction (trans_id,user_id,amount,created_on,song_id) VALUES ('" . $transactionid . "','$user_id','" . $amount . "','" . $transactiontime . "','" . $song_id . "')");

        $buffer = array("status_code" => 200);
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    print json_encode($buffer);
} elseif ($type == "download") {

    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);

    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $song_id = $db->safesql(trim(intval($_REQUEST ['song_id'])));

        $row1 = $db->super_query("SELECT * FROM vass_transaction WHERE song_id = '$song_id'");
        $row2 = $db->super_query("SELECT price  FROM vass_pricedetails WHERE content='song'");


        if ($row1['song_id']) {
            $buffer = array(
                "status_text" => "Payed",
                "status_code" => 200,
                "song_price" => intval($row2['price']));
        } else {
            $buffer = array("status_text" => "Not Payed", "status_code" => 100);
        }
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    print json_encode($buffer);
} /* album download 
 * @Author:Sibani Mishra
 * @date: 21stAugust  
 */ elseif ($type == "downloadalbum") {


    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);



    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = $user_id");


    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

        $album_id = $db->safesql(trim(intval($_REQUEST ['album_id'])));

        $row1 = $db->super_query("SELECT * FROM vass_analz WHERE album_id = $album_id");

        $row2 = $db->super_query("SELECT price FROM vass_pricedetails WHERE content='album'");
        if ($row1['album_id']) {
            $buffer = array(
                "status_text" => "Payed",
                "status_code" => 200,
                "album_price" => intval($row2['price']));
        } else {
            $buffer = array("status_text" => "Not Payed", "status_code" => 100);
        }
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    print json_encode($buffer);
} /* for forgotpassword
 * @Author:Sibani Mishra
 * @date: 18stAugust  
 */ elseif ($type == "forgetpassword") {

//    $mailer = new Mandrill("lSqqGC9W5IZbmrOzyY60cA"); //akash
    $mailer = new Mandrill("vGlh3WAlVEtKcQVMz5Fjig");
    $email = $db->safesql($_REQUEST['email']);
    $email = filter_var($email, FILTER_SANITIZE_EMAIL);

    $row = $db->super_query("SELECT * FROM vass_users WHERE email = '$email'");
    $alphanumerics = 'ABCDE01234FGHIJ56789KLMNO01234PQRST56789UVWXY01234Z56789';
    $shuffled = str_shuffle($alphanumerics);
    $optCode = substr($shuffled, 0, 16);
    $digits10 = substr($optCode, 0, 10);
    $digits6 = substr($optCode, 10, 6);

    $expiretime = time() + 600;

    $row1 = $db->query("UPDATE vass_users SET opt  = '" . $optCode . "', expiretime  = " . '' . $expiretime . " WHERE email = '" . filter_var($email, FILTER_SANITIZE_EMAIL) . "'");


    $responseObj = new stdClass();
    if ($row) {
        $template_name = 'KiandastreamForgotPassword';
        $subject = 'change the password';
        $mergers = array(
            array(
                'name' => 'email',
                'content' => $email
            ),
            array(
                'name' => 'otp',
                'content' => $digits6
            ),
            array(
                'name' => 'expiretime',
                'content' => $expiretime
            )
        );

        $message = array
            (
            'html' => null,
            'text' => null,
            'subject' => $subject,
            'from_email' => 'sibanimishra@globussoft.com',
            'from_name' => 'kiandastream_support',
            to => array(
                array(
                    'email' => $email,
                    'Name' => "Kianda User"
                )
            ),
            'headers' => array('Reply-To' => 'vinidubey@globussoft.com'),
            'important' => false,
            'track_opens' => null,
            'track_clicks' => null,
            'auto_text' => null,
            'auto_html' => null,
            'inline_css' => null,
            'url_strip_qs' => null,
            'preserve_recipients' => null,
            'bcc_address' => 'message.bcc_address@example.com',
            'tracking_domain' => null,
            'signing_domain' => null,
            'global_merge_vars' => $mergers,
            'merge' => true
        );


        $async = false;
        $result = $mailer->messages->sendTemplate($template_name, null, $message, $async);

//        echo "<pre>";
//        print_r($result);
//        die;
        if ($result[0]["status"] == "sent") {
            $responseObj->status_code = 200;
            $responseObj->data = $digits10;
        } else {
            $responseObj->status_code = 198;
            $responseObj->data = "Please try again";
        }
        echo json_encode($responseObj);
    } else {

        $responseObj->status_code = 198;
        $responseObj->data = "email id not exist";
        echo json_encode($responseObj);
    }
} /* for otp verification 
 * @Author:Sibani Mishra
 * @date: 18stAugust  
 */ elseif ($type == "verifyotp") {

    $responseObj = new stdClass();
    $email = $db->safesql($_REQUEST['email']);
    $email = filter_var($email, FILTER_SANITIZE_EMAIL);


    $digits6 = $db->safesql($_REQUEST['optmail']);
    $digits10 = $db->safesql($_REQUEST['optresponse']);

    $finaloptcode = $digits10 . $digits6;
    //echo "SELECT opt FROM vass_users WHERE email = '" . $email . "' AND opt='" . $finaloptcode . "' AND expiretime > ". time();
    //die;
    $row = $db->super_query("SELECT opt FROM vass_users WHERE email = '" . $email . "' AND opt='" . $finaloptcode . "' AND expiretime > " . time());


    if ($row) {
        $responseObj->status_code = 200;
        $responseObj->data = $finaloptcode;
    } else {
        $responseObj->status_code = 198;
        $responseObj->data = 'otp is invalid';
    }
    echo json_encode($responseObj);
}
/* for change password 
 * @Author:Sibani Mishra
 * @date: 18stAugust  
 */ elseif ($type == "changepassword") {
    $responseObj = new stdClass();
    $email = filter_var($db->safesql($_REQUEST['email']), FILTER_SANITIZE_EMAIL);
    $optCode = $db->safesql($_REQUEST['finaloptcode']);
    $password = $db->safesql(md5($_REQUEST['password']));

    $row1 = $db->query("UPDATE vass_users SET password  = '" . $password . "' WHERE email = '" . filter_var($email, FILTER_SANITIZE_EMAIL) . "' AND opt ='" . $optCode . "' ");

    if ($row1) {
        $responseObj->status_code = 200;
        $responseObj->data = 'password changed successfully';
    } else {

        $responseObj->status_code = 198;
        $responseObj->data = 'Unable to change password';
    }
    echo json_encode($responseObj);
}/* To get  all albumlist of an artist 
 * @Author:Sibani Mishra
 * @date: 21stAugust  
 */ else if ($type == "featuredalbum") {
    $responseObj = new stdClass();
    $today = date("Y-m-d");
    $row = array();
    $i = 1;

    do {
        $row = $db->super_query("SELECT album_id ,count(*) as coun FROM vass_analz WHERE time <= NOW() AND time >= DATE_SUB(NOW(), INTERVAL " . (7 * $i) . " DAY) GROUP BY album_id order by coun DESC");
        $i++;
        if (count($row) > 0)
            break;
    }while (count($row) > 0);

    $album = $db->super_query("SELECT vass_albums.name AS album_title, vass_albums.descr, vass_albums.date, vass_artists.name AS artist_name,vass_artists.id AS artist_id,vass_albums.id AS album_id FROM vass_albums LEFT JOIN vass_artists ON vass_albums.artist_id = vass_artists.id WHERE vass_albums.id='" . $row["album_id"] . "'");

    $sql_result = $db->query("SELECT vass_songs.id AS song_id, vass_songs.title AS song_title, vass_songs.loved, vass_artists.name AS song_artist,vass_artists.id AS artist_id,vass_albums.name AS song_album, vass_albums.id AS album_id FROM vass_songs LEFT JOIN vass_albums ON vass_songs.album_id = vass_albums.id LEFT JOIN vass_artists ON vass_songs.artist_id = vass_artists.id WHERE vass_albums.id = '" . $row["album_id"] . "'");

    while ($row = $db->get_row($sql_result)) {
        $song_list ['artist_id'] = $row ['artist_id'];
        $song_list ['album'] = $row ['song_album'];
        $song_list ['similar_artists'] = similar_artists($row ['song_id']);
        $song_list ['buy_link'] = null;
        $song_list ['artist'] = $row ['song_artist'];
        $song_list ['url'] = stream($row ['song_id']);
        $song_list ['image'] = songlist_images($row ['album_id']);
        $song_list ['title'] = $row ['song_title'];
        $song_list ['metadata_state'] = metadata_state($row ['song_id']);
        $song_list ['sources'] = sources($row ['song_id']);
        $song_list ['viewer_love'] = viewer_love($row ['song_id']);
        $song_list ['last_loved'] = null;
        $song_list ['recent_loves'] = recent_loves($row ['song_id']);
        $song_list ['aliases'] = aliases($row ['song_id']);
        $song_list ['loved_count'] = $row ['loved'];
        $song_list ['id'] = $row ['song_id'];
        $song_list ['tags'] = tags($row ['song_id']);
        $song_list ['album_id'] = $row ['album_id'];
        $song_list ['trending_rank_today'] = trending_rank_today($row ['song_id']);
        $songs [] = $song_list;
    }

    $buffer = array("status_text" => "OK", "status_code" => 200, "results" => 1, "start" => 0, "total" => 1, "albums" => array("description" => $album ['descr'], "artist" => $album ['artist_name'], "date" => date('D M d Y H:i:s O', strtotime($album ['date'])), "artwork_url" => $config['siteurl'] . "static/albums/" . $config['album_week'] . "_extralarge.jpg", "title" => $album ['album_title'], "day" => 20111005, "songs" => $songs));

    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
}/* To get sorted Artist list with in a  single page
 * @Author:Sibani Mishra
 * @date: 31stAugust 
 */ else if ($type == "artist_list") {
    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);
    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");

    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {
        $page = intval($_REQUEST['page']);
    }
    if (!empty($user_id)) {
        $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_artists");
        $total_results = $total_results['count'];
    }

    $limit = 20;
    $newpage = $page * $limit;

    if ($page > 1 && $newpage <= $total_results) {
        $previous = $page - 1;
    }
    if ($newpage < $total_results) {
        $next = $page + 1;
    }
    if ($page > 1) {
        $page = ($page - 1) * $limit;
    } else {
        $page = 0;
    }
    if (!empty($user_id)) {
        $artist = $db->query("SELECT id,name FROM vass_artists ORDER BY name LIMIT $limit OFFSET $page ");
    }


    while ($row = $db->get_row($artist)) {

        $num_songs = $db->super_query("SELECT COUNT(*) AS count FROM vass_songs WHERE artist_id = '" . $row['id'] . "'");

        $row['total_songs'] = $num_songs['count'];

        $abc[] = $row;
    }
    if (isset($abc)) {
        $buffer = array();

        $buffer = array("status_code" => 200, "status_text" => "OK", "artists" => $abc, "total" => $total_results, "next" => $next, "previous" => $previous);
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
}


/* To get all sorted Artist list
 * @Author:Sibani Mishra
 * @date: 31stAugust
 */ else if ($type == "browseartists") {
    $user_id = intval($_REQUEST['user_id']);

    $token = $db->safesql($_REQUEST['access_token']);
    $row = $db->super_query("SELECT token FROM vass_session WHERE user_id = '" . $user_id . "'");


    if ($token == $row['token'] && (!empty($token)) && (!empty($user_id))) {

    }
    if (!empty($user_id)) {
        $total_results = $db->super_query("SELECT COUNT(*) AS count FROM vass_artists");

        $total_results = $total_results['count'];
    }

    if (!empty($user_id)) {
        $artist = $db->query("SELECT id,name FROM vass_artists ORDER BY name");
    }


    while ($row = $db->get_row($artist)) {

        $num_songs = $db->super_query("SELECT COUNT(*) AS count FROM vass_songs WHERE artist_id = '" . $row['id'] . "'");

        $row['total_songs'] = $num_songs['count'];

        $abc[] = $row;
    }
    if (isset($abc)) {
        $buffer = array();

        $buffer = array("status_code" => 200, "status_text" => "OK", "artists" => $abc, "total" => $total_results, "next" => $next, "previous" => $previous);
    } else {
        header("HTTP/1.0 401 UNAUTHORIZED");

        $buffer ['status_code'] = 401;

        $buffer ['status_text'] = "Authentication required.";
    }
    header('Cache-Control: no-cache, must-revalidate');

    header('Content-type: application/json');

    print json_encode($buffer);
}

?>