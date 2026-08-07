<template>
  <div class="daily-quote" v-if="quote">
    <span class="quote-icon">💡</span>
    <p class="quote-text">"{{ quote.text }}"</p>
    <span class="quote-author">— {{ quote.author }}</span>
    <el-button
      class="quote-refresh"
      :icon="Refresh"
      text
      size="small"
      @click="shuffle"
      title="换一句"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'

const quotes = [
  { text: '代码是写给人看的，顺便能在机器上运行。', author: 'Harold Abelson' },
  { text: '简单是可靠的先决条件。', author: 'Edsger Dijkstra' },
  { text: '先让程序跑起来，再考虑优化。', author: 'Kent Beck' },
  { text: '任何傻瓜都能写出计算机能理解的代码，只有优秀的程序员才能写出人能理解的代码。', author: 'Martin Fowler' },
  { text: '测试可以证明 Bug 的存在，但不能证明 Bug 不存在。', author: 'Edsger Dijkstra' },
  { text: '编程不是打字，是思考。', author: 'Rich Hickey' },
  { text: '软件就像熵：难以掌握，没有重量，并且遵从热力学第二定律。', author: 'Norman Augustine' },
  { text: '好的程序员是那些在写代码之前先思考的人。', author: 'Steve McConnell' },
  { text: '最大的性能优化，是把不必要做的事去掉。', author: 'Raymond Chen' },
  { text: '编程的艺术就是组织复杂性的艺术。', author: 'Edsger Dijkstra' },
  { text: '永远不要停止学习，因为技术在不断演进。', author: 'Linus Torvalds' },
  { text: '复制粘贴不是编程，理解才是。', author: '佚名' },
  { text: 'Bug 是程序员的勋章，修复它们是荣耀。', author: '佚名' },
  { text: '一个优秀的程序员，一年能创造 100 万美元的价值。', author: 'Google 哲学' },
  { text: '天下武功，唯快不破；编程之道，唯简至上。', author: '中国程序员哲学' },
]

function shuffle() {
  const idx = Math.floor(Math.random() * quotes.length)
  quote.value = quotes[idx]
}

const quote = ref(null)
shuffle() // Init with random quote
</script>

<style scoped>
.daily-quote {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 20px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.06), rgba(118, 75, 162, 0.06));
  border-left: 3px solid var(--color-primary);
  border-radius: 8px;
  margin-bottom: 20px;
  transition: all 0.3s;
}

html.dark .daily-quote {
  background: rgba(255, 255, 255, 0.03);
}

.quote-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.quote-text {
  margin: 0;
  font-size: 14px;
  color: var(--color-text);
  flex: 1;
  line-height: 1.5;
  font-style: italic;
}

.quote-author {
  font-size: 12px;
  color: var(--color-text-secondary);
  white-space: nowrap;
  flex-shrink: 0;
}

.quote-refresh {
  flex-shrink: 0;
  opacity: 0.4;
  transition: opacity 0.2s;
}

.quote-refresh:hover {
  opacity: 1;
}
</style>
