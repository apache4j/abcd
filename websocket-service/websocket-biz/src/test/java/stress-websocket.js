// stress-websocket-multiip.js
//npm install ws
/**
* 主要是模拟客户端压测
* 1.打开本地端口限制
* sysctl net.inet.ip.portrange.first
* sysctl net.inet.ip.portrange.last
* sudo sysctl -w net.inet.ip.portrange.first=10000
* sudo sysctl -w net.inet.ip.portrange.last=65535
* 2. 运行脚本
* node stress-websocket.js
**/
const cluster = require('cluster');
const os = require('os');
const WebSocket = require('ws');
const net = require('net');

const URL = 'ws://127.0.0.1:8090/ws?p=dddddd&t=guest';
//const URL = 'wss://www.wintoeg.com/api/ws?p=dddddd&t=guest';

// -------- 压测参数 --------
const TOTAL_CONNECTIONS = 50000;   // 总连接数
const CONNECTION_INTERVAL = 200;     // 建立连接间隔 (ms)
const HEARTBEAT_INTERVAL = 15000;  // 心跳间隔 (ms)
const TEST_DURATION = 60 * 60 * 1000; // 压测时长 (60分钟)
//const WORKERS = os.cpus().length;  // 默认使用 CPU 核心数
const WORKERS = 4;//默认启动4个进程
// 多个本地 IP 地址（需要提前绑定到网卡）
//const LOCAL_IPS = [
 // '127.0.0.1'
//];

//const LOCAL_IPS = [
//  '192.168.30.101',
//  '192.168.30.102',
//  '192.168.30.103',
//  '192.168.30.157'
//];

//const LOCAL_IPS = [
//  '192.168.30.157'
//];


// -------- Master 进程 --------
if (cluster.isMaster) {
  //console.log(`🚀 压测启动: ${TOTAL_CONNECTIONS} 连接, ${WORKERS} 进程, 本地IP: ${LOCAL_IPS.join(', ')}`);
console.log(`🚀 压测启动: ${TOTAL_CONNECTIONS} 连接, ${WORKERS} 进程`);
  const perWorker = Math.floor(TOTAL_CONNECTIONS / WORKERS);

  for (let i = 0; i < WORKERS; i++) {
    cluster.fork({ 
      CONNECTIONS: perWorker,
      //LOCAL_IPS: JSON.stringify(LOCAL_IPS)
    });
  }

  cluster.on('exit', (worker, code, signal) => {
    console.log(`❌ Worker ${worker.process.pid} 退出`);
  });

} else {
  // -------- Worker 进程 --------
  const CONNECTIONS = parseInt(process.env.CONNECTIONS, 10);
 // const ips = JSON.parse(process.env.LOCAL_IPS);
  let active = 0;

  //let ipIndex = 0; // 轮询选择 IP

  function createConnection(id) {
    //const localAddress = ips[ipIndex % ips.length];
    //ipIndex++;

    const ws = new WebSocket(URL, {
      agent: new (require('https').Agent)({
        //localAddress: localAddress // 绑定源 IP
      })
    });
	//const socket = net.connect({ host: '127.0.0.1', port: 8090, localAddress });
	
 // const ws = new WebSocket(URL, { socket });

    ws.on('open', () => {
      active++;
      ws.heartbeat = setInterval(() => {
        if (ws.readyState === WebSocket.OPEN) {
          ws.send(JSON.stringify({ type: 'ping', clientId: id }));
        }
      }, HEARTBEAT_INTERVAL);
    });

    ws.on('close', () => {
      active--;
      clearInterval(ws.heartbeat);
    });

    ws.on('error', (err) => {
		console.error(`WebSocket 错误: ${err.message}`)
      if (err.code !== 'EADDRNOTAVAIL') {
        console.error(`错误 ${id}:`, err.message);
      }
    });
  }

  (async () => {
    for (let i = 0; i < CONNECTIONS; i++) {
      createConnection(i);
      if (i % 500 === 0) {
        console.log(`[PID ${process.pid}] 已建立 ${i} 连接`);
      }
      await new Promise(r => setTimeout(r, CONNECTION_INTERVAL));
    }

    console.log(`✅ Worker ${process.pid} 建立完毕 ${CONNECTIONS} 个连接`);

    setInterval(() => {
      console.log(`[PID ${process.pid}] 活跃连接: ${active}`);
    }, 10000);

    setTimeout(() => {
      console.log(`⏰ Worker ${process.pid} 压测结束`);
      process.exit(0);
    }, TEST_DURATION);
  })();
}
