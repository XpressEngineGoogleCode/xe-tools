<?php
$argv = $_SERVER['argv'];
$path = $argv[1];
if(!$path){
	echo "lang2xml.php [path of lang directory]";
	exit;
}

$depth=0;
$pos = array();

function getDir($path){
	$paths = array();
	$t = explode('/',$path);
	if(array_pop($t) =='lang') $paths[] = $path;

	foreach(glob($path.'/*',GLOB_ONLYDIR) as $p) {
		$paths= array_merge($paths, getDir($p));
	}

	return $paths;
}


function loadLang($path){
	global $langs;

	foreach(glob($path.'/*.lang.php') as $file){
		$file_name = basename($file);
		$lang_code = preg_replace('/([^.]+)\.lang.php/','$1',$file_name);
		$lang = new stdClass;
		include $file;
		${$lang_code} = clone $lang;

		$langs[$lang_code] = ${$lang_code};
	}

	$xml = "<?xml version='1.0' encoding='UTF-8'?>\n<lang>";
	$xml .= makeNode($langs['ko'],'ko');
	$xml .= "\n</lang>";

	return $xml;
}

function makeNode($lang, $lang_code, $k=""){
	global $depth,$pos,$langs;

	$lang_types = array('en' ,'jp' ,'zh-CN' ,'zh-TW' ,'fr' ,'de' ,'ru' ,'es' ,'tr' ,'vi' ,'mn');

	$pos[] = $k;
	if($pos[count($pos)-2]=="['") $pos[] = "']";

	if(!is_array($lang) && !is_object($lang)) {

		$xml =  sprintf("\n".'%s<value xml:lang="%s"><![CDATA[%s]]></value>',str_repeat("\t",$depth+1), $lang_code, $lang);

		eval(sprintf('$en = $langs[\'%s\']%s;', 'en', join('',$pos)));
		eval(sprintf('$ko = $langs[\'%s\']%s;', 'ko', join('',$pos)));

		foreach($lang_types as $lang_type){
			$str = sprintf('$value = $langs[\'%s\']%s;', $lang_type, join('',$pos));
			eval($str);
		
			if($value) {
				if($lang_type!='en' &&($en == $value || $ko==$value)){
				}
				else{
					$xml .=  sprintf("\n".'%s<value xml:lang="%s"><![CDATA[%s]]></value>',str_repeat("\t",$depth+1), $lang_type, $a);
				}
			}
			/*else {
				if($en){
					$xml .=  sprintf("\n".'%s<value xml:lang="%s"><![CDATA[%s]]></value>',str_repeat("\t",$depth+1), $c, $en);
				}else{
					$xml .=  sprintf("\n".'%s<value xml:lang="%s"><![CDATA[%s]]></value>',str_repeat("\t",$depth+1), $c, $lang);
				}
			}*/
		}


	} else {
		$depth++;
		if(is_object($lang)){
			$pos[] = '->';
			$lang = get_object_vars($lang);
		}else{
			$pos[] = "['";
		}

		$xml = '';
		foreach($lang as $key => $val){
			if(is_array($val)){
				$type=' type="array"';
			} else {
				$type='';
			}

			$xml .= sprintf("\n". '%s<item name="%s"%s>',str_repeat("\t",$depth), $key, $type);
			$xml .= makeNode($val, $lang_code, $key);
			$xml .= "\n".str_repeat("\t",$depth)."</item>";

		}
		$depth--;

		array_pop($pos);
	}
	$t = array_pop($pos);
	if($t=="']") array_pop($pos);
	
	return $xml;

}


echo "lang2xml target path : ",$path,"\n\n"; 
$paths = getDir($path);

foreach($paths as $path){
	$xml = loadLang($path);
	$xml_file = $path.'/lang.xml';
	file_put_contents($xml_file, $xml);
	echo "create file :",$xml_file,"\n";"
}

