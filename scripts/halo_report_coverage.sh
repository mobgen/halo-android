#!/bin/bash
if [ $# -eq 3 ]; then
  FILE_COVERAGE=$1
  PLATFORM_NAME=$2
  REPORT_LOCATION=$3
  REPORT_FILE_NAME="coverage.report"

  # Extract the code coverage
  CODECOV=$(xmllint --html -xpath "//table/tfoot/tr/td[3]/text()[1]" $FILE_COVERAGE | sed -e "s/%//g")

  # Send the report to the dashboard
  BODY="code_coverage,platform=$PLATFORM_NAME coverage=$CODECOV"
  echo "Coverage for $PLATFORM_NAME: $CODECOV"
  curl -XPOST 'http://halo-dashboard.aws.mobgen.com:8086/write?db=halo' --data-binary "$BODY"

  # Put it in the reports
  if [ ! -d $REPORT_LOCATION ]; then
    mkdir -p $REPORT_LOCATION;
  fi
  echo "$PLATFORM_NAME : $CODECOV" >> "$REPORT_LOCATION/$REPORT_FILE_NAME"
else
	echo "Invalid script parameters:"
  echo "Param 1: FILE_TO_ANALYZE"
  echo "Param 2: PLATFORM_NAME"
  echo "Param 3: REPORT_LOCATION"
  exit -1
fi