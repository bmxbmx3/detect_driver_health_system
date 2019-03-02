import schedule
import time
import datetime
from vehicle import sqlOperation


def up_syn_data():
    """
    用户同步数据更新函数：分析出每个用户每天的状态，并存储用户请求时需要返回的字符串
    :return:
    """
    # [(30.0, 70.01, '2018-7-26'), (27.81, 70.0, '2018-7-29')]
    get_data = sqlOperation.wait_syn_data()
    i = ([0, 0, 0], [0, 0, 0], [0, 0, 0], [0, 0, 0], [0, 0, 0], [0, 0, 0], [0, 0, 0])
    for up_all_times in range(7):
        p_data = ((datetime.datetime.today() - datetime.timedelta(days=6 - up_all_times)).strftime('%m-%d')) \
            .split('-')
        i[up_all_times][0] = "{}月{}日".format(p_data[0].replace('0', ''), p_data[1])
    for up_times in get_data:
        sub_days = (datetime.datetime.now() - datetime.datetime.strptime(up_times[2], '%Y-%m-%d')).days
        i[6 - sub_days][1] = up_times[1]
        i[6 - sub_days][2] = up_times[0]
        # print(up_times)
        # print(6-sub_days)
    save_str = "同步成功+{}+{}+{}+{}+{}+{}+{}+" \
               "{}+{}+{}+{}+{}+{}+{}+" \
               "{}+{}+{}+{}+{}+{}+{}+体温偏低，心跳正常！建议添加衣物，防止感冒！" \
        .format(i[0][0], i[1][0], i[2][0], i[3][0], i[4][0], i[5][0], i[6][0],
                i[0][1], i[1][1], i[2][1], i[3][1], i[4][1], i[5][1], i[6][1],
                i[0][2], i[1][2], i[2][2], i[3][2], i[4][2], i[5][2], i[6][2])
    # print(save_str)
    # print(datetime.datetime.now().strftime('%Y-%m-%d'))
    # print((datetime.datetime.now()-datetime.datetime.strptime(get_data[1][2], '%Y-%m-%d')).days)
    sqlOperation.insert_syn_data('user', save_str)


def every_up():
    """
    每天定时执行用户同步数据更新函数：up_syn_data()
    :return:
    """
    schedule.every(1 / 12).minutes.do(up_syn_data)
    # schedule.every().day.at("04:30").do(up_syn_data())
    # while True:
    #     schedule.run_pending()
    #     time.sleep(1)
    up_syn_data()


if __name__ == '__main__':
    every_up()
