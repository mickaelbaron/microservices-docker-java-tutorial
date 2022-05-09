<script setup>
import { defineProps } from 'vue'

defineProps({ messages: Array })
</script>

<template>
  <div class="card">
    <h5 class="card-header">Liste des Messages</h5>
    <div class="card-body">
      <table class="table table-hover">
        <thead>
          <tr>
            <th scope="col">Id</th>
            <th scope="col">Message</th>
            <th scope="col">Date</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="val in messages" :key="val.rid">
            <th scope="row">{{ val.rid }}</th>
            <td>{{ val.message }}</td>
            <td>{{ val.startDate }}</td>
          </tr>
        </tbody>
      </table>
      <button type="button" @click="$emit('messages')" class="btn btn-danger">
        Rafraîchissement
      </button>
    </div>
  </div>
</template>
