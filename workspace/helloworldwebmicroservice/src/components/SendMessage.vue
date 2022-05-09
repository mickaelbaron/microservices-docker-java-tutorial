<script setup>
import { defineEmits, ref } from 'vue'

const message = ref()
const emit = defineEmits(['sendMessage'])

function sendMessage() {
  emit('sendMessage', message.value)
}
</script>

<template>
  <div class="card">
    <h5 class="card-header">Formulaire</h5>
    <div class="card-body">
      <div class="mb-3">
        <label for="exampleInputEmail1" class="form-label"
          >Veuillez saisir un message de Bienvenue</label
        >
        <input
          type="email"
          class="form-control"
          id="exampleInputEmail1"
          v-model="message"
          aria-describedby="emailHelp"
        />
        <div class="invalid-feedback">Veuillez renseigner ce champ.</div>
      </div>
      <button type="button" @click="sendMessage" class="btn btn-primary">
        Envoyer message
      </button>
    </div>
  </div>
</template>
