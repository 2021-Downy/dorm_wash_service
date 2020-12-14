<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');
    
    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
    
    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android ){
	$user_num    = $_POST['user_num'];
	
    $stmt = $con->prepare('select ID, name, phone_num, dorm_num from USER where user_num=:user_num');
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
                'ID'=>$ID,
                'name'=>$name,
                'phone_num'=>$phone_num,
                'dorm_num'=>$dorm_num
            ));
        }

        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }

?>
