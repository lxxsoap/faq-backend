#!/bin/bash -e
# 查找进程
pid=$(ps -ef | grep pybbs.jar | grep -v grep | awk '{print $2}')

if [ -z "$pid" ]; then
    echo "没有找到 pybbs.jar 相关进程"
    exit 0
fi

echo "找到 pybbs.jar 进程，PID: $pid"

# 先尝试正常停止
echo "正在停止进程..."
kill $pid

# 等待进程停止
for i in {1..30}; do
    if ! ps -p $pid > /dev/null; then
        echo "进程已成功停止"
        exit 0
    fi
    echo "等待进程停止...$i"
    sleep 1
done

# 如果进程仍然存在，强制停止
if ps -p $pid > /dev/null; then
    echo "进程未能正常停止，正在强制停止..."
    kill -9 $pid
    if ! ps -p $pid > /dev/null; then
        echo "进程已强制停止"
    else
        echo "停止进程失败"
        exit 1
    fi
fi