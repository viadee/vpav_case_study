# vPAV Example Process
This is an example process to present the [vPAV](https://github.com/viadee/vPAV/tree/development)

## Requirements
Therefore you have to download the following maven projects and run this projects as maven install (make sure to package as jar):

- [parent_config](https://github.com/viadee/vPAV_parent_config)
- [external Checker example](https://github.com/viadee/vPAV_checker_plugin_example)

## Metrics

|Performance Metric    |  #1   |  #2   |  #3   |  #4   |  #5   |  #6   |  #7   |  #8   |  #9   |  #10  |
| -------------------- |------:|------:|------:|------:|------:|------:|------:|------:|------:|------:|
| CPU Usage (in %)     | 23,4  | 21,4  | 23,0  | 15,3  | 22,5  | 20,7  | 27,4  | 22,5  | 22,0  | 23,7  |
| Memory Usage (in mb) | 114,3 | 104,1 | 165,1 | 166,9 | 146,0 | 166,1 | 84,9  | 82,2  | 115,5 | 121,4 |
| Duration (in sec)    | 3,274 | 3,490 | 2,999 | 3,40  | 3,277 | 3,153 | 3,324 | 3,290 | 3,207 | 3,258 |
| Classes loaded       | 8414  | 9231  | 8641  | 8851  | 8740  | 8958  | 9149  | 8865  | 9195  | 8787  |

### Lines of Code etc.

#### viasurance-kfzglasbruch-process
- BPMN Processes: 2  
- BPMN Elements:  
  -- AllChecker.bpmn: 132  
  -- KfzGlasbruch.bpmn: 30  
- Classes implemented: 28  
- Lines of Code: 1010
#### viasurance-fachklassen
- Classes implemented: 11  
- Lines of Code: 255
#### viasurance-webservices
- Classes implemented: 6  
- Lines of Code: 120
#### viasrepair-abrechnungssystem
- Classes implemented: 8  
- Lines of Code: 521
#### Total
- BPMN Elements: 162  
- Classes implemented: 53  
- Lines of Code: 1906  

### Equipment:  
- MacBook Pro (15", 2019)  
- Intel Core i9 (8 Core with 2,3 Ghz)  
- 16 GB 2400 MHz DDR4
