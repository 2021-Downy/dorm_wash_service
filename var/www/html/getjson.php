<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');
    
    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
    
    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android ){
	$ID    = $_POST['ID'];
	$pw    = $_POST['pw'];
	
    $stmt = $con->prepare('select * from USER where ID=:ID and pw=:pw');
    $stmt->bindParam(':ID', $ID);
    $stmt->bindParam(':pw', $pw);
    $stmt->execute();
    }

    if ($stmt->rowCount() > 0)
    {
        $data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC))
        {
            extract($row);
    
            array_push($data, 
                array('ID'=>$ID,
                'pw'=>$pw,
                'name'=>$name,
                'dorm_num'=>$dorm_num
            ));
        }

        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }

?>
