<?php

$skip_dirs = array(
	'classes',
	'common/css.backup',
	'common/js/unittest',
	'config',
	'files',
	'libs',
	'tests',
	'test-phpUnit',
	'tools',
    'widgets',
    'widgetstyles',
	'layouts',
	'm.layouts',
	'themes',
	'modules/board',
	'modules/homepage',
	'modules/nspam',
	'modules/resource',
);

$target_extensions = array('js', 'css', 'jpeg', 'png', 'gif', 'jpg');
$skip_files = array('.', '..', '.svn', 'skins');

if ($argc < 3) die("사용법: php getfile.php 원본디렉터리 타겟디렉터리\n");

$source_dir = $argv[1];
$target_dir = $argv[2];

if (!is_dir($source_dir)) die("원본 디렉터리가 올바르지 않습니다.\n");

$path_info = pathinfo($source_dir);
$source_dir = $path_info['dirname'];
if ($path_info['filename'])	$source_dir .= "/{$path_info['filename']}";
$base_dir = $source_dir;

$path_info = pathinfo($target_dir);
$target_dir = $path_info['dirname'];
if ($path_info['filename'])	$target_dir .= "/{$path_info['filename']}";

search($source_dir);

// 디렉토리 탐색
function search($source_dir){
	global $target_dir, $base_dir;
	global $skip_files, $skip_dirs, $target_extensions;

	$dir = opendir($source_dir);
	if (!$dir) die("디렉토리 오픈 실패\n");

	while(false !== ($file = readdir($dir))){
		if (in_array($file, $skip_files)) continue;
		if (in_array(str_replace("$base_dir/", '', "$source_dir/$file"), $skip_dirs)) continue;

		if (is_dir("$source_dir/$file")){
			search("$source_dir/$file");
		}else{
			$path_info = pathinfo($file);
			$extention = strtolower($path_info['extension']);
			if (!in_array($extention, $target_extensions)) continue;

			// 복사
			mycopy("$source_dir/$file");
		}
	}

	closedir($dir);
}

// 복사
function mycopy($source_file){
	global $target_dir, $base_dir;

	$target_file = "$target_dir/" . str_replace("$base_dir/", '', $source_file);
	$path_info = pathinfo($target_file);
	makeDir($path_info['dirname']);
	@unlink($target_file);
	copy($source_file, $target_file);
	echo "$target_file\n";
}

function makeDir($path_string) {
	$path_list = explode('/', $path_string);

	if ($path_list[0] == '.' || $path_list[0] == '..')
		$path = '';
	else
		$path = '/';

	for($i=0;$i<count($path_list);$i++) {
		if(!$path_list[$i]) continue;
		$path .= $path_list[$i].'/';

		if(!is_dir($path)) {
			mkdir($path, 0755);
			chmod($path, 0755);
		}
	}

	return is_dir($path_string);
}
