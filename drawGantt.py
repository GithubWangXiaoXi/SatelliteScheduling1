import sys
import plotly.express as px
import pandas as pd
import plotly.figure_factory as ff
import json

def json2List(jsonPath):
    '''
    :param jsonPath: json文件路径
    :return:
    '''

    load_list = None
    with open(jsonPath, 'r') as load_f:
        load_list = json.load(load_f)
    return load_list

def list2Jobs(jobs_list):
    '''
    :param jobs_list: json2List的返回结果
            {'location': ['61.28', '33.07_E', '162.42_S'], 'name': 'Job1', 'priority': 2,
            'procedures':
                [
                    {'duration': 299, 'endT': '2021-05-26 00:05:22', 'startT': '2021-05-26 00:00:00'},
                    {'duration': 366, 'endT': '2021-05-26 00:15:05', 'startT': '2021-05-26 00:05:22'},
                    {'duration': 365, 'endT': '2021-05-26 00:28:33', 'startT': '2021-05-26 00:15:05'},
                    {'duration': 375, 'endT': '2021-05-26 00:39:41', 'startT': '2021-05-26 00:28:33'},
                    {'duration': 201, 'endT': '2021-05-26 00:44:38', 'startT': '2021-05-26 00:39:41'},
                    {'duration': 208, 'endT': '2021-05-26 00:49:38', 'startT': '2021-05-26 00:44:38'},
                    {'duration': 389, 'endT': '2021-05-26 01:02:39', 'startT': '2021-05-26 00:49:38'}
                ],
            'resolution': 10, 'sensor': 1}
    :return: 返回dict_list
            每个job有多个工序，将每个工序和job绑定，并返回每条工序结果：
            dict(Task="Job A", Start='2021-05-26 00:00:00', Finish='2021-05-26 00:05:00',Resource="A"),
            Note：可以添加其他附加信息
    '''
    dict_list = []
    for job in jobs_list:

        procedures = job["procedures"]  # 此时的job是dict, procedures是list
        del job["procedures"]  # 删除掉job的procedures键值对，为了和之后的每个procedure拼接

        for procedure in procedures:
            dict_temp = {}  # 用来拼接新的dict
            dict_temp.update(job)
            dict_temp.update(procedure)
            dict_list.append(dict_temp)

    return dict_list


def list2Satellites(satellite_list):
    '''
    :param satellite_list: json2List的返回结果
                [
                  {
                    "mR": 5,
                    "mTI": 40,
                    "name": "satellite1",
                    "sensors": [
                      2
                    ],
                    "timeWindows": [
                      {
                        "endT": "2021-05-26 00:07:27",
                        "location": [
                          "40.37",
                          "31.03_W",
                          "125.24_S"
                        ],
                        "startT": "2021-05-26 00:00:00"
                      },
                      {
                        "endT": "2021-05-26 00:12:40",
                        "location": [
                          ".95",
                          "44.21_W",
                          "30.77_N"
                        ],
                        "startT": "2021-05-26 00:07:27"
                      },
                      {
                        "endT": "2021-05-26 00:19:13",
                        "location": [
                          ".95",
                          "44.21_W",
                          "30.77_N"
                        ],
                        "startT": "2021-05-26 00:12:40"
                      },
                      {
                        "endT": "2021-05-26 00:22:46",
                        "location": [
                          "40.37",
                          "31.03_W",
                          "125.24_S"
                        ],
                        "startT": "2021-05-26 00:19:13"
                      },
                      {
                        "endT": "2021-05-26 00:30:12",
                        "location": [
                          "40.37",
                          "31.03_W",
                          "125.24_S"
                        ],
                        "startT": "2021-05-26 00:22:46"
                      },
                      {
                        "endT": "2021-05-26 00:37:09",
                        "location": [
                          "40.37",
                          "31.03_W",
                          "125.24_S"
                        ],
                        "startT": "2021-05-26 00:30:12"
                      },
                      {
                        "endT": "2021-05-26 00:43:54",
                        "location": [
                          ".95",
                          "44.21_W",
                          "30.77_N"
                        ],
                        "startT": "2021-05-26 00:37:09"
                      },
                      {
                        "endT": "2021-05-26 00:47:42",
                        "location": [
                          "3.15",
                          "7.55_E",
                          "93.47_N"
                        ],
                        "startT": "2021-05-26 00:43:54"
                      },
                      {
                        "endT": "2021-05-26 00:55:32",
                        "location": [
                          "3.15",
                          "7.55_E",
                          "93.47_N"
                        ],
                        "startT": "2021-05-26 00:47:42"
                      },
                      {
                        "endT": "2021-05-26 01:00:57",
                        "location": [
                          "3.15",
                          "7.55_E",
                          "93.47_N"
                        ],
                        "startT": "2021-05-26 00:55:32"
                      }
                    ]
                  }
                ]
    :return: 返回dict_list
            每个卫星有多个连续的时间窗，每条返回结果类似：
             dict(Task="Satellite 1", Start='2021-05-26 00:00:00', Finish='2021-05-26 00:05:00',Resource="A")
             Note：可以添加其他附加信息
    '''
    dict_list = []
    for satellite in satellite_list:

        timeWindows = satellite["timeWindows"]  # 此时的job是dict, procedures是list
        del satellite["timeWindows"]  # 删除掉job的procedures键值对，为了和之后的每个procedure拼接

        for timeWindow in timeWindows:
            dict_temp = {}  # 用来拼接新的dict
            dict_temp.update(satellite)
            dict_temp.update(timeWindow)
            dict_list.append(dict_temp)

    return dict_list

import time

'''
@link: https://www.runoob.com/python/att-time-strptime.html
'''
def str2Time(string):
    '''
    :param str: 格式为'%Y-%m-%d %H:%M:%S'
    :return:
    '''
    date = time.strptime(string, '%Y-%m-%d %H:%M:%S')
    return date

def get_df_from_jobDictList(job_dict_list):
    '''
    :param job_dict_list
            [
              {
                'location': [
                  '61.28',
                  '33.07_E',
                  '162.42_S'
                ],
                'name': 'Job1',
                'priority': 2,
                'resolution': 10,
                'sensor': 1,
                'duration': 299,
                'endT': '2021-05-26 00:05:22',
                'startT': '2021-05-26 00:00:00'
              },
              {
                'location': [
                  '61.28',
                  '33.07_E',
                  '162.42_S'
                ],
                'name': 'Job1',
                'priority': 2,
                'resolution': 10,
                'sensor': 1,
                'duration': 366,
                'endT': '2021-05-26 00:15:05',
                'startT': '2021-05-26 00:05:22'
              }
            ]
    :return: df每一列由如下dict组成
             dict(Task="Job A", Start='2021-05-26 00:00:00', Finish='2021-05-26 00:05:00',Resource="A",hover_data="A"),
    '''
    df = pd.DataFrame()
    for dict_t in job_dict_list:
        temp = {}
        temp['Task'] = dict_t['name']
        temp['Start'] = dict_t['startT']
        temp['Finish'] = dict_t['endT']

        temp['Resource'] = str(dict_t['location'])  # 主要是用来排序

        del dict_t['name']
        del dict_t['startT']
        del dict_t['endT']
        del dict_t['location']

        temp['hover_name'] = str(dict_t)
        # temp['A'] = f"priority = {dict_t['priority']}"
        df = df.append(temp, ignore_index=True)

    return df

def get_df_from_satelliteDictList(satellite_dict_list):
    '''
    :param satellite_dict_list
            [
                {'mR': 5,
                 'mTI': 40,
                 'name': 'satellite1',
                 'sensors': [2],
                 'endT': '2021-05-26 00:07:27',
                 'location': ['40.37', '31.03_W', '125.24_S'],
                 'startT': '2021-05-26 00:00:00'
                 }
            ]
    :return: df每一列由如下dict组成
             dict(Task="Satellite A", Start='2021-05-26 00:00:00', Finish='2021-05-26 00:05:00',Resource="A",hover_data="A"),
    '''
    df = pd.DataFrame()
    for dict_t in satellite_dict_list:
        temp = {}
        temp['Task'] = dict_t['name']
        temp['Start'] = dict_t['startT']
        temp['Finish'] = dict_t['endT']

        temp['Resource'] = str(dict_t['location'])  # 主要是用来排序

        del dict_t['name']
        del dict_t['startT']
        del dict_t['endT']
        del dict_t['location']

        temp['hover_name'] = str(dict_t)
        # temp['A'] = f"priority = {dict_t['priority']}"
        df = df.append(temp, ignore_index=True)

    return df


def draw_gantt(df,mode = 1):
    '''
    :param df: df默认值为：
            df = pd.DataFrame([
               dict(Task="Job A", Start='2021-05-26 00:00:00', Finish='2021-05-26 00:05:00',Resource="A",hover_name='A'),
               dict(Task="Job B", Start='2021-05-26 00:10:03', Finish='2021-05-26 00:12:03',Resource="B",hover_name='B'),
               dict(Task="Job C", Start='2021-05-26 00:20:35', Finish='2021-05-26 00:21:35',Resource="C",hover_name='C')
             ])
             Note：resource主要是用来排序
    :return:
    '''
    if mode == 1:
        fig = px.timeline(
            df,
            x_start="Start",
            x_end="Finish",
            y="Task",
            color="Resource",
            hover_name="hover_name"
            # text="A",
        )
        # otherwise tasks are listed from the bottom up
        fig.update_yaxes(autorange="reversed")
        fig.show()

    if mode == 2:
        colors = ('rgb(46, 137, 205)',
                  'rgb(114, 44, 121)',
                  'rgb(198, 47, 105)',
                  'rgb(58, 149, 136)',
                  'rgb(107, 127, 135)',
                  'rgb(46, 180, 50)',
                  'rgb(150, 44, 50)',
                  'rgb(100, 47, 150)',
                  'rgb(58, 100, 180)',
                  'rgb(150, 127, 50)')

        fig = ff.create_gantt(df, colors=colors, index_col='Resource',bar_width=0.4,
                              title='任务初始情况', show_colorbar=True,
                              group_tasks=True, showgrid_x=True, showgrid_y=True)
        fig.show()

if __name__ == '__main__':

    arg1 = sys.argv[1]
    arg2 = sys.argv[2]
    # jsonPath = "../resources/jobs.json"
    jsonPath = arg1
    job_list = json2List(jsonPath)
    dict_list = list2Jobs(job_list)
    df = get_df_from_jobDictList(dict_list)

    # jsonPath = "../resources/satellites.json"
    jsonPath = arg2
    satellite_list = json2List(jsonPath)
    dict_list = list2Satellites(satellite_list)
    df1 = get_df_from_satelliteDictList(dict_list)

    df1 = df1.append(df)
    draw_gantt(df1)