import pandas as pd
import matplotlib.pyplot as plt
from os import listdir
from os.path import isfile, join

def read_timestamps_files():
    path = './timestamps/'
    onlyfiles = [f for f in listdir(path) if isfile(join(path, f))]
    dfs = []

    for file in onlyfiles:
        dfs.append(pd.read_csv(f'{path}{file}',sep='\s+',header=None, names=['time_producer', 'time_consumer', 'bytes']))

    return dfs

def append_dataframes(dfs):
    first_df = dfs[0]

    for df in dfs:
        if df is first_df:
            continue

        first_df = first_df.append(df)

    print(first_df)

    return first_df

def get_mbps_latency(df):
    mbps = []
    latency = []

    start_timestamp = df.iloc[0]['time_consumer']
    size_in_bytes = 0
    max_latency_per_second = 0

    for index, row in df.iterrows():
        # if len(mbps) == 60 and len(latency) == 60:
        #     break

        size_in_bytes += row['bytes']
        end_timestamp = row['time_consumer']
        current_latency = (row['time_consumer'] - row['time_producer']) / 1000

        if current_latency > max_latency_per_second:
            max_latency_per_second = current_latency

        is_one_second_interval = (end_timestamp - start_timestamp) >= 1000

        if is_one_second_interval:
            # one megabit is 125000 bytes
            mbps.append(size_in_bytes / 125000)
            size_in_bytes = 0
            start_timestamp = row['time_consumer']

            latency.append(max_latency_per_second)
            max_latency_per_second = 0

    return (mbps, latency)

def plot_latency(latency):
    plt.plot(latency)
    plt.ylabel('Latency in seconds')
    plt.xlabel('Time in seconds')
    plt.show()

def plot_mbps(list_mbps):
    plt.plot(list_mbps)
    plt.ylabel('Mbps')
    plt.xlabel('Time in seconds')
    plt.show()

def plot(mbps, latencies):
    plot_latency(latencies)
    plot_mbps(mbps)

def init():
    dfs = read_timestamps_files()
    df = append_dataframes(dfs)

    df.sort_values(['time_producer'], inplace=True)

    plot(*get_mbps_latency(df))

init()
