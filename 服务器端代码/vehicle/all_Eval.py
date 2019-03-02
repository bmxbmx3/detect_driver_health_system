

def syn_ev_al(temp_now, heart_now):
    temp_status = temp_ev_al(temp_now)
    heart_status = heart_ev_al(heart_now)
    # # syn_score = (temp_status[0] + heart_status[0])/2
    # syn_score = (temp_status + heart_status) / 2
    # # syn_msg = '{}{}'.format(temp_status[1], heart_status[1])
    # if syn_score >= 7:
    #     syn_score_msg = '优'
    # elif 7 > syn_score >= 5:
    #     syn_score_msg = '良'
    # else:
    #     syn_score_msg = '差'
    syn_score_msg = [temp_status, heart_status]
    return syn_score_msg
        # [syn_score_msg, syn_msg]


def temp_ev_al(temp_now):
    # msg = ['您的体温较低，请及时增加衣服，建议打开车内空调，预防低体温症！“低体温症”发病的原因主要是因为有些人体内产热少，体温调节功能差。平时注意运动。',
    #        # '您的体温较高，可能由于天气炎热（请打开车内空调），或是发低烧！ 请按时服药，及时就医，尽量不要开车。',
    #        # '您的体温太高！身体状况已达高烧，请及时就医！建议您此时不要开车，高烧使得大脑反应迟钝，请到附近医院就医，多喝热水多休息。',
    #        # '体温状态良好，请继续保持。']
    if temp_now < 36:
        return "差"
        # [5, msg[0]]
    elif 38.1 > temp_now > 37.3:
        return "良"
        # [5, msg[1]]
    elif temp_now >= 38.1:
        return "差"
        # [0, msg[2]]
    else:
        return "优"
        # [10, msg[3]]

# hearts=xlsread('heartsingle.xlsx');
# row=xlsread('rows.xlsx');
# fid = fopen('heartsingle.txt','wt');
# if hearts >= 60&&hearts <= 100
#     if (hearts >= (50+10*row ))&& (hearts <=(60+10*row))
#         fprintf(fid,'      本次心率数据处于个人正常水平，请继续保持！\n');
#     else
#         fprintf(fid,'      本次心率数据处于正常水平，较平时稍有波动，无需担忧。\n');
#     end
# end


def heart_ev_al(heart_now):
    # try:
    #     pass
    # except Exception as ret:
    #     print('当前数据库无数据，无法计算个人平均水平')
    # else:
    #     pass
    # msg = ['本次心率数据处于正常水平，请继续保持。',
    #        '您的心率小于60bpm，心跳过缓',
    #        '您的心率小于50bpm，可能是由于窦性心动过缓，注意伤寒！若出现胸闷、乏力、头晕等症状，请及时就医',
    #        '您的心率大于160bpm，可能是以下因素引起，请及时检查！高热、贫血、缺氧、感染、甲状腺机能亢进、疼痛、急性风湿热、脚气病及神经官能症等',
    #        '您的心率大于100bpm，心跳过快']
    if 60 <= heart_now <= 100:
        return "优"
        # [10, msg[0]]
    elif 50 <= heart_now <= 60:
        return "良"
        # [5, msg[1]]
    elif 30 <= heart_now < 50:
        return "差"
        # [2, msg[2]]
    elif heart_now > 160:
        return "差"
        # [2, msg[3]]
    elif 100 < heart_now <= 160:
        return "良"
    elif heart_now < 30:
        return "异常"
        # [5, msg[4]]


if __name__ == '__main__':
    fin = syn_ev_al(37, 70)
    print('评级为：{}'.format(fin))
