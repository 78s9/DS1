/**
 * useWebSocket — Reactive WebSocket composable
 * Features: auto-reconnect, heartbeat, message queue, status tracking
 *
 * @param {string} url - WebSocket endpoint
 * @param {Object} options
 * @returns {{ status, lastMessage, messages, send, connect, disconnect }}
 */
import { ref, onUnmounted, readonly } from 'vue'

const WS_STATUS = {
  CONNECTING: 'CONNECTING',
  OPEN: 'OPEN',
  CLOSING: 'CLOSING',
  CLOSED: 'CLOSED'
}

export function useWebSocket(url, options = {}) {
  const {
    autoConnect = true,
    reconnectInterval = 3000,
    maxReconnectAttempts = 10,
    heartbeatInterval = 15000,
    heartbeatMessage = 'ping',
    onMessage = null
  } = options

  const status = ref(WS_STATUS.CLOSED)
  const lastMessage = ref(null)
  const messages = ref([])
  const reconnectAttempts = ref(0)
  const maxMessages = options.maxMessages || 200

  let ws = null
  let heartbeatTimer = null
  let reconnectTimer = null

  function startHeartbeat() {
    stopHeartbeat()
    heartbeatTimer = setInterval(() => {
      if (ws && ws.readyState === WebSocket.OPEN) {
        ws.send(typeof heartbeatMessage === 'function' ? heartbeatMessage() : heartbeatMessage)
      }
    }, heartbeatInterval)
  }

  function stopHeartbeat() {
    if (heartbeatTimer) {
      clearInterval(heartbeatTimer)
      heartbeatTimer = null
    }
  }

  function scheduleReconnect() {
    if (reconnectAttempts.value >= maxReconnectAttempts) {
      console.warn(`[useWebSocket] Max reconnect attempts (${maxReconnectAttempts}) reached`)
      status.value = WS_STATUS.CLOSED
      return
    }

    reconnectTimer = setTimeout(() => {
      reconnectAttempts.value++
      connect()
    }, reconnectInterval)
  }

  function connect() {
    if (ws && (ws.readyState === WebSocket.OPEN || ws.readyState === WebSocket.CONNECTING)) {
      return
    }

    status.value = WS_STATUS.CONNECTING

    try {
      ws = new WebSocket(url)
    } catch (err) {
      console.error('[useWebSocket] Connection failed:', err)
      status.value = WS_STATUS.CLOSED
      scheduleReconnect()
      return
    }

    ws.onopen = () => {
      status.value = WS_STATUS.OPEN
      reconnectAttempts.value = 0
      startHeartbeat()
    }

    ws.onmessage = (event) => {
      let data
      try {
        data = JSON.parse(event.data)
      } catch {
        data = event.data
      }
      lastMessage.value = data
      messages.value.push(data)
      // Trim message history
      if (messages.value.length > maxMessages) {
        messages.value = messages.value.slice(-maxMessages)
      }
      if (onMessage) onMessage(data)
    }

    ws.onclose = () => {
      status.value = WS_STATUS.CLOSED
      stopHeartbeat()
      scheduleReconnect()
    }

    ws.onerror = (err) => {
      console.error('[useWebSocket] Error:', err)
      // onclose will fire after this
    }
  }

  function disconnect() {
    stopHeartbeat()
    if (reconnectTimer) {
      clearTimeout(reconnectTimer)
      reconnectTimer = null
    }
    reconnectAttempts.value = maxReconnectAttempts // Prevent auto-reconnect
    if (ws) {
      ws.close(1000, 'Client disconnect')
      ws = null
    }
    status.value = WS_STATUS.CLOSED
  }

  function send(data) {
    if (ws && ws.readyState === WebSocket.OPEN) {
      const payload = typeof data === 'object' ? JSON.stringify(data) : data
      ws.send(payload)
    } else {
      console.warn('[useWebSocket] Cannot send — socket not open')
    }
  }

  // Auto-connect
  if (autoConnect) {
    connect()
  }

  // Cleanup on component unmount
  onUnmounted(() => {
    disconnect()
  })

  return {
    status: readonly(status),
    lastMessage: readonly(lastMessage),
    messages: readonly(messages),
    reconnectAttempts: readonly(reconnectAttempts),
    send,
    connect,
    disconnect
  }
}
