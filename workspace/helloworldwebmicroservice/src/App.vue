<script setup>
import { ref } from 'vue'

import SendMessage from './components/SendMessage.vue'
import ListMessages from './components/ListMessages.vue'

const messages = ref([])
const restHostUrl = 'http://localhost:8080/helloworld'

function updateMessages() {
  let request = new Request(restHostUrl, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json'
    }
  })

  fetch(request)
    .then((response) => {
      if (response.ok) {
        return response.json()
      } else {
        console.log('Problem to retrieve messages')
      }
    })
    .then((data) => {
      messages.value = data
    })
    .catch((error) => {
      console.log(error)
    })
}

function sendMessages(newValue) {
  let request = new Request(restHostUrl, {
    method: 'POST',
    body: JSON.stringify({ message: newValue }),
    headers: {
      'Content-Type': 'application/json'
    }
  })

  fetch(request).then((response) => {
    if (response.ok) {
      console.log('Message sent:' + newValue)
      updateMessages()
    } else {
      console.log('Problem to send message')
    }
  })
}

updateMessages()
</script>

<template>
  <div class="container py-4">
    <header class="pb-3 mb-4 border-bottom">
      <h1>HelloWorld Client</h1>
    </header>

    <div>
      <div class="mb-4"><SendMessage @send-message="sendMessages" /></div>
      <div>
        <ListMessages :messages="messages" @messages="updateMessages" />
      </div>
    </div>

    <footer class="pt-3 mt-4 text-muted border-top">
      <p class="text-center">Mickaël BARON - Cours SOA - Microservices</p>
    </footer>
  </div>
</template>
