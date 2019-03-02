import socket
import threading
import time
# import os
from vehicle import sqlOperation
from vehicle import all_Eval

client_socket_list = []


def tcp_server_start(port):
    """
    功能函数，TCP服务端开启的方法
    :return: None
    """
    tcp_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # 取消主动断开连接四次握手后的TIME_WAIT状态
    tcp_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    # 设定套接字为非阻塞式
    tcp_socket.setblocking(False)
    try:
        tcp_socket.bind(('', port))
    except Exception as ret:
        msg = '请检查端口号\n'
        print(msg)
    else:
        tcp_socket.listen()
        sever_th = threading.Thread(target=tcp_server_concurrency(tcp_socket))
        sever_th.start()
        msg = 'TCP服务端正在监听端口:%s\n' % str(port)
        print(msg)


def tcp_server_concurrency(tcp_socket):
    """
    功能函数，供创建线程的方法；
    使用子线程用于监听并创建连接，使主线程可以继续运行，以免无响应
    使用非阻塞式并发用于接收客户端消息，减少系统资源浪费，使软件轻量化
    将接收到的数据存储在MySQL中
    :return:None
    """
    while True:
        try:
            client_socket, client_address = tcp_socket.accept()
        except Exception as ret:
            time.sleep(0.001)
        else:
            client_socket.setblocking(False)
            # 将创建的客户端套接字存入列表,client_address为ip和端口的元组
            client_socket_list.append((client_socket, client_address))
            msg = 'TCP IOT Server 已连接IP:%s端口:%s\n' % client_address
            print(msg)
        # 轮询客户端套接字列表，接收数据
        for client, address in client_socket_list:
            try:
                recv_msg = client.recv(1024)
            except Exception as ret:
                time.sleep(0.001)
            else:
                if recv_msg:
                    judge_msg = recv_msg.decode('utf_8')
                    if judge_msg[0] == '同':
                        try:
                            client.send(sqlOperation.finial_syn_data('user')[0][1].encode('utf-8'))
                        except Exception as ret:
                            print('同步数据失败')
                            client.send('同步数据失败'.encode('utf-8'))
                    elif judge_msg[0] == '身':
                        user_status = judge_msg.split('+')
                        try:
                            client.send((sqlOperation.judge_user_T(user_status[1], user_status[2])).encode('utf-8'))
                        except Exception as ret:
                            print('身份验证失败')
                            client.send('用户不存在'.encode('utf-8'))
                    elif judge_msg[0] == '注':
                        user_status = judge_msg.split('+')
                        try:
                            client.send((sqlOperation.add_user_T(user_status[1], user_status[2], user_status[3],
                                                                 user_status[4])).encode('utf-8'))
                        except Exception as ret:
                            print('用户注册失败')
                            client.send('用户注册失败'.encode('utf-8'))
                    elif judge_msg[0] == '测':
                        user_status = judge_msg.split('+')
                        try:
                            sqlOperation.insert_status_data(user_status[1], user_status[2],
                                                            user_status[3], 25, 0, user_status[6])
                        except Exception as ret:
                            print('发送方数据有误')
                            client.send('数据上传失败，请检查格式或者时间是否有问题！'.encode('utf-8'))
                        else:
                            # 对数据进行分析
                            send_msg = all_Eval.syn_ev_al(float(user_status[2]), float(user_status[3]))
                            # 返回分析结果
                            fin_send_msg = '体温{}心率{}'.format(send_msg[0], send_msg[1])
                            client.send(fin_send_msg.encode('utf-8'))
                    else:
                        print("非本服务器业务")
                        client.send("非本服务器业务".encode('utf-8'))
                else:
                    client.close()
                    client_socket_list.remove((client, address))


if __name__ == '__main__':
    # pid = os.fork()
    # if pid == 0:
    #     print('I am child process (%s) and my parent is %s.' % (os.getpid(), os.getppid()))
    # else:
    tcp_server_start(1000)
