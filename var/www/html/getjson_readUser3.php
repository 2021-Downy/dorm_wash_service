<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');

    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android ){
    $user_num = $_POST['user_num'];

    $stmt = $con->prepare('SELECT COUNT(CASE WHEN DAYOFWEEK(date)=2 THEN 1 END) AS mon,
    COUNT(CASE WHEN DAYOFWEEK(date)=3 THEN 1 END) AS tue,
    COUNT(CASE WHEN DAYOFWEEK(date)=4 THEN 1 END) AS wed,
    COUNT(CASE WHEN DAYOFWEEK(date)=5 THEN 1 END) AS thu,
    COUNT(CASE WHEN DAYOFWEEK(date)=6 THEN 1 END) AS fri,
    COUNT(CASE WHEN DAYOFWEEK(date)=7 THEN 1 END) AS sat,
    COUNT(CASE WHEN DAYOFWEEK(date)=1 THEN 1 END) AS sun,

    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=00 THEN 1 END) AS t0,

    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=01 THEN 1 END) AS t1,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=02 THEN 1 END) AS t2,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=03 THEN 1 END) AS t3,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=04 THEN 1 END) AS t4,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=05 THEN 1 END) AS t5,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=06 THEN 1 END) AS t6,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=07 THEN 1 END) AS t7,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=08 THEN 1 END) AS t8,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=09 THEN 1 END) AS t9,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=10 THEN 1 END) AS t10,

    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=11 THEN 1 END) AS t11,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=12 THEN 1 END) AS t12,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=13 THEN 1 END) AS t13,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=14 THEN 1 END) AS t14,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=15 THEN 1 END) AS t15,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=16 THEN 1 END) AS t16,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=17 THEN 1 END) AS t17,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=18 THEN 1 END) AS t18,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=19 THEN 1 END) AS t19,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=20 THEN 1 END) AS t20,

    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=21 THEN 1 END) AS t21,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=22 THEN 1 END) AS t22,
    COUNT(CASE WHEN SUBSTRING(start_time, 12, 2)=23 THEN 1 END) AS t23

    FROM USES
    WHERE user_num=:user_num');
    $stmt->bindParam(':user_num', $user_num);
    $stmt->execute();
    }

    if ($stmt->rowCount() > 0)
    {
        $data = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC))
        {
            extract($row);

            array_push($data,
                array(
                  'mon'=>$mon,
                  'tue'=>$tue,
                  'wed'=>$wed,
                  'thu'=>$thu,
                  'fri'=>$fri,
                  'sat'=>$sat,
                  'sun'=>$sun,

                  't0'=>$t0,

                  't1'=>$t1,
                  't2'=>$t2,
                  't3'=>$t3,
                  't4'=>$t4,
                  't5'=>$t5,
                  't6'=>$t6,
                  't7'=>$t7,
                  't8'=>$t8,
                  't9'=>$t9,
                  't10'=>$t10,

                  't11'=>$t11,
                  't12'=>$t12,
                  't13'=>$t13,
                  't14'=>$t14,
                  't15'=>$t15,
                  't16'=>$t16,
                  't17'=>$t17,
                  't18'=>$t18,
                  't19'=>$t19,
                  't20'=>$t20,

                  't21'=>$t21,
                  't22'=>$t22,
                  't23'=>$t23
            ));
        }

        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }

?>
