<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');
    
    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
    
    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android ){
	$ID    = $_POST['ID'];
	
    $stmt = $con->prepare('select ID from USER where ID=:ID');
    $stmt->bindParam(':ID', $ID);
    $stmt->execute();
    }

	if ($stmt!=null){
		if ($stmt->rowCount() > 0)
		{
        
			header('Content-Type: application/json; charset=utf8');
			$json = json_encode($stmt, JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
			echo $json;
		}
	}
?>
