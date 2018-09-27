start=$SECONDS
file="closest-pair-out.txt"
while IFS=('/' ' ' ':' '.') read var1 var2 var3 var4 var5 var6 var7 check
do
        # display $line or do somthing with $line
answer=$(java ClosestPoints < "$var5-tsp.txt")
checksum=$(bc <<< "$answer - $check")
if (( $(echo "$checksum < 0.00001" |bc -l) )) && (( $(echo "$checksum > -0.00001" |bc -l) ));
then 
echo "CORRECT" 
else
echo "FALSE" 
fi
done <"$file"
duration=$(( SECONDS - start ))
echo $duration
