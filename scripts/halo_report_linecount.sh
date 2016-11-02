#!/bin/bash
if [ $# -eq 4 ]; then
	DIRECTORY_TO_EVALUATE=$1
	EXCLUDE_REGEXP=$2
	SLOC_LOCATION=$3
	REPORT_LOCATION=$4
	REPORT_FILE_NAME="lines.report"
	if [ "$(type -t $SLOC_LOCATION/node_modules/sloc/bin/sloc)" != "file" ]; then
		npm install --prefix $SLOC_LOCATION sloc
	fi

	# Calculate the lines
	$($SLOC_LOCATION/node_modules/sloc/bin/sloc -k total,source,comment -e $EXCLUDE_REGEXP $DIRECTORY_TO_EVALUATE >> sloc.tmp) 
	TOTAL=$(cat sloc.tmp | grep 'Physical' | awk '{print $3}')
	SOURCE=$(cat sloc.tmp | grep 'Source' | awk '{print $3}')
	COMMENTS=$(cat sloc.tmp | grep 'Comment' | awk '{print $3}')
	rm -rf sloc.tmp

	# Report the lines
	echo "Total lines: "$TOTAL "Source lines: "$SOURCE "Comment lines: "$COMMENTS

	# Report to dashboard
	BODY="code_size,platform=android total=$TOTAL,source=$SOURCE,comments=$COMMENTS"
  curl -XPOST 'http://halo-dashboard.aws.mobgen.com:8086/write?db=halo' --data-binary "$BODY"

	# Report to file
	if [ ! -d $REPORT_LOCATION ]; then
    mkdir -p $REPORT_LOCATION;
  fi
	rm -rf "$REPORT_LOCATION/$REPORT_FILE_NAME"
	echo $TOTAL $SOURCE $COMMENTS >> "$REPORT_LOCATION/$REPORT_FILE_NAME"
else
	echo "Invalid script params:"
	echo "Param 1: DIRECTORY_TO_EVALUATE"
	echo "Param 2: EXCLUDE_REGEXP"
	echo "Param 3: SLOC_LOCATION"
	echo "Param 4: REPORT_LOCATION"
	exit -1
fi