##elasticsearch测试脚本
# $1: 索引名称
# $2: json文件
#!/bin/sh
IPPORT=localhost:9200
jsonDir=$(dirname $0)/jsons

if [ -z $1 ];then
	echo "Usage1: $0   indexName    jsonFile"
	echo "Usage2: $0  预设命令 参数 "
	echo "      支持的预设命令:"
	echo -e "       STATUS \n       GETMAPPING index \n       IK_MAX \n       IK_SMART" 
	exit 1
fi

#一些预设的简单查询
#获取集群信息
STATUS(){
	echo curl -XGET ${IPPORT}
	curl -XGET ${IPPORT}
}
#创建索引结构
PUTMAPPING(){
	curl -XPUT -H "Content-Type: application/json"  ${IPPORT}/${$1}   -d @$2
}
#获取索引结构
GETMAPPING(){
	#jq进行格式化
	echo curl -XGET ${IPPORT}/$1/_mapping |jq
	curl -XGET ${IPPORT}/$1/_mapping |jq
	echo 
	return 0
}
#创建索引别名
CREATEALIAS(){
	curl -XPUT  ${IPPORT}/$1/_alias/$2
}
#IK分词器两种模式测试
IK_MAX(){
	echo curl -XPOST -H "Content-Type: application/json"  $IPPORT/_analyze -d  '{"analyzer": "ik_max_word","text":"中华人民共和国人民大会堂"}'
	curl -XPOST -H "Content-Type: application/json"  $IPPORT/_analyze -d '{"analyzer": "ik_max_word","text":"中华人民共和国人民大会堂"}'|jq
}
IK_SMART(){
	echo curl -XPOST -H "Content-Type: application/json"  $IPPORT/_analyze -d  '{"analyzer": "ik_smart","text":"中华人民共和国人民大会堂"}'
	curl -XPOST -H "Content-Type: application/json"  $IPPORT/_analyze -d '{"analyzer": "ik_smart","text":"中华人民共和国人民大会堂"}'|jq
}

#执行预设命令
if [ "STATUS" = $1 ];then
	STATUS
	exit $?
#创建索引结构 $2=索引名称 $3=json文件
elif [ "PUTMAPPING" = $1 ];then
	PUTMAPPING $2 $3
	exit $?
#获取索引结构  $2=索引名称
elif [ "GETMAPPING" = $1 ];then
	GETMAPPING $2
	exit $?
#创建索引别名
elif [ "CREATEALIAS" = $1 ];then
	CREATEALIAS $2 $3
	exit $?
#IK分词结果测试 
elif [ "IK_MAX" = $1 ];then
	IK_MAX 
	exit $?
elif [ "IK_SMART" = $1 ];then
	IK_SMART 
	exit $?
fi

cd $jsonDir

#执行命令-只支持查询 其他操作语句格式不一，放到预设命令
echo curl -X POST -H "\"Content-Type: application/json\"" $IPPORT/$1/_search -d @$2 
curl -X POST -H "Content-Type: application/json" $IPPORT/$1/_search -d @$2 >/tmp/res.txt

#jq进行格式化
cat /tmp/res.txt|jq
echo  
