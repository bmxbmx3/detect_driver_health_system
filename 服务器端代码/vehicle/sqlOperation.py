import mysql.connector
# import datetime
# import time


def insert_status_data(user, tmp, heart, age, sex, time):
    conn = mysql.connector.connect(host='123.206.109.175', user='root', db='vehicle', passwd='hhywx201798', port=3306)
    cursor = conn.cursor()
    add_data = ("insert into UserStatus "
                "(User, Temp, Heart, Age, Sex, Time) "
                "values (%s,%s,%s,%s,%s,%s)")
    cursor.execute(add_data, (user, tmp, heart, age, sex, time))
    # 通过rowcount获得插入的行数: print('rowcount =', cursor.rowcount)
    cursor.close()
    conn.commit()
    conn.close()


def insert_per_status(user_status, cursor):
    insert_status = ("insert into UserStatus "
                     "(User, Temp, Heart, Age, Sex, Time) "
                     "values (%s, %s, %s, %s, %s, %s)")
    for per_user_status in user_status:
        print(per_user_status)
        try:
            cursor.execute(insert_status, per_user_status)
        except mysql.connector.Error as err:
            print(err.msg)


def insert_all_status(_user_status):
    conn = mysql.connector.connect(host='123.206.109.175', user='root', db='vehicle', passwd='hhywx201798', port=3306)
    cursor = conn.cursor()
    insert_per_status(_user_status, cursor)
    cursor.close()
    conn.commit()
    conn.close()


def query_data(req_type):
    conn = mysql.connector.connect(host='123.206.109.175', user='root', db='vehicle', passwd='hhywx201798', port=3306)
    cursor = conn.cursor()
    req_sql = 'SELECT {} FROM UserStatus'.format(req_type)
    cursor.execute(req_sql)
    req_data_f = cursor.fetchall()
    # 通过rowcount获得插入的行数: print('rowcount =', cursor.rowcount)
    cursor.close()
    conn.commit()
    conn.close()
    return req_data_f


def wait_syn_data():
    """
    获取用户最近7天的数据用于进一步分析以及同步数据
    :return:
    """
    conn = mysql.connector.connect(host='123.206.109.175', user='root', db='vehicle', passwd='hhywx201798', port=3306)
    cursor = conn.cursor()
    req_sql = (
        "SELECT max( temp ), max( heart ), easy_status.post_date FROM "
        "( SELECT heart, temp, date_format( Time, '%Y-%c-%d' ) AS post_date FROM UserStatus ) "
        "easy_status WHERE DATE_SUB( NOW( ), INTERVAL 7 DAY ) <= date( easy_status.post_date ) "
        "GROUP BY easy_status.post_date ORDER BY easy_status.post_date")
    cursor.execute(req_sql)
    req_data_f = cursor.fetchall()
    # 通过rowcount获得插入的行数: print('rowcount =', cursor.rowcount)
    cursor.close()
    conn.commit()
    conn.close()
    return req_data_f


def insert_syn_data(user, syn_data):
    """
    刷新每天的同步信息
    :param user:
    :param syn_data:
    :return:
    """
    conn = mysql.connector.connect(host='123.206.109.175', user='root', db='vehicle', passwd='hhywx201798', port=3306)
    cursor = conn.cursor()
    # 新用户插入数据，老用户只是每天进行更新
    add_data = ("INSERT INTO user_syn_data (user_syn_data.`User`,user_syn_data.syn_data) "
                "VALUES	( '{}', '{}') ON DUPLICATE KEY UPDATE `User`='{}',syn_data ='{}'") \
        .format(user, syn_data, user, syn_data)
    cursor.execute(add_data)
    # 通过rowcount获得插入的行数: print('rowcount =', cursor.rowcount)
    cursor.close()
    conn.commit()
    conn.close()


def finial_syn_data(_need_user):
    """
    查询user_syn_data表，返回用于同步的数据
    :param _need_user:
    :return:
    """
    conn = mysql.connector.connect(host='123.206.109.175', user='root', db='vehicle', passwd='hhywx201798', port=3306)
    cursor = conn.cursor()
    req_sql = "SELECT * FROM user_syn_data WHERE !STRCMP(User,'{}')".format(_need_user)
    cursor.execute(req_sql)
    req_data_f = cursor.fetchall()
    # 通过rowcount获得插入的行数: print('rowcount =', cursor.rowcount)
    cursor.close()
    conn.commit()
    conn.close()
    return req_data_f


def get_registered_user(registered_user):
    conn = mysql.connector.connect(host='123.206.109.175', user='root', db='vehicle', passwd='hhywx201798', port=3306)
    cursor = conn.cursor()
    req_sql = "SELECT * FROM register_table WHERE !STRCMP(User,'{}')".format(registered_user)
    cursor.execute(req_sql)
    req_data_f = cursor.fetchall()
    # 通过rowcount获得插入的行数: print('rowcount =', cursor.rowcount)
    cursor.close()
    conn.commit()
    conn.close()
    return req_data_f


def judge_user_T(verify_user, verify_passwd):
    real_passwd = get_registered_user(verify_user)[0][1]
    if real_passwd == verify_passwd:
        verify_result = "用户存在且密码正确"
    else:
        verify_result = "用户存在且密码错误"
    return verify_result


def add_user_T(register_user, passwd, sex, year):
    conn = mysql.connector.connect(host='123.206.109.175', user='root', db='vehicle', passwd='hhywx201798', port=3306)
    cursor = conn.cursor()
    # 新用户插入数据，老用户只是每天进行更新
    add_data = ("INSERT INTO register_table (user, passwd, sex, year) "
                "VALUES	( '{}', '{}', '{}', {})").format(register_user, passwd, sex, year)
    cursor.execute(add_data)
    # 通过rowcount获得插入的行数: print('rowcount =', cursor.rowcount)
    cursor.close()
    conn.commit()
    conn.close()
    return '注册成功'


if __name__ == '__main__':
    """
    此处进行函数功能测试
    """
    # 测试主函数
    # insert_status_data(5, 'User', 37.23, 80, 25, 0, datetime.datetime.now())
    # user_status = []
    # for i in range(5):
    #     each_data = (5, 'User', 37.23, 80, 25, 0, datetime.datetime.now())
    #     user_status.append(each_data)
    #     time.sleep(1)
    # insert_all_status(user_status)

    # req_data = query_data('Temp')
    # print(req_data[0][0]+1)
    get_data = wait_syn_data()
    print(get_data)
    # 更新数据函数测试
    # insert_syn_data('wx', '12345654321')

    # print(finial_syn_data('user'))
    # add_user_T('user123', 'passwd', "男", 18)
    # print(get_registered_user('user')[0][1])
    # print(judge_user_T('user', 'passwd'))