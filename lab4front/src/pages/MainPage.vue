<template>
  <table class="main-structure-table">
    <tbody>
    <tr>
      <td class="control-panel">
        <button type="button" class="btn reset-colors material-icons">logout</button>
        <div id="click_catcher">
          <img src="/img/graph.svg">
        </div>
        <form>
          <div class="X_select">
            <p class="prompt-text">Введите X (-4 ... 4):</p>
            <div class="box-visual invert-colors">
              <div v-for="i in 9" :key="i">
                <input type="radio" name="x" :id="'x_' + (i - 5).toString().replace('-', '_')" :value="i - 5" v-model="xValue">
                <label :for="'x_' + (i - 5).toString().replace('-', '_')">{{ i - 5 }}</label>
              </div>
            </div>
          </div>
          <div class="Y_select">
            <p class="prompt-text">Введите Y (-5 ... 3):</p>
            <input type="text" class="text-input" placeholder="Введите значение..." v-model="yValue" >
          </div>
          <div class="R_select" id="click_catcher2">
            <p class="prompt-text">Введите R (-4 ... 4):</p>
            <div class="box-visual invert-colors">
              <div v-for="i in 9" :key="i">
                <input type="radio" name="r" :id="'r_' + (i - 5).toString().replace('-', '_')" :value="i - 5" v-model="rValue">
                <label :for="'r_' + (i - 5).toString().replace('-', '_')">{{ i - 5 }}</label>
              </div>
            </div>
          </div>
          <br/>
          <button class="btn confirm-colors material-icons">send</button>
          <button class="btn reset-colors material-icons" v-on:click="thing">backspace</button>
        </form>
      </td>
      <td class="indent-box">
        <table class="result-table" id="result">
          <thead>
          <tr>
            <td>X</td>
            <td>Y</td>
            <td>R</td>
            <td>Входит?</td>
            <td>Время</td>
            <td>Выполнение</td>
          </tr>
          </thead>
        </table>
      </td>
    </tr>
    </tbody>
  </table>
</template>

<script>
export default {
  name: "MainPage",
  data() {
    return {
      xValue: '',
      yValue: '',
      rValue: '',
      thing: () => console.log('hello world')
    }
  }
}
</script>

<style scoped>

</style>