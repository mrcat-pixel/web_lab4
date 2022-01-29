<template>
  <table class="main-structure-table">
    <tbody>
    <tr>
      <td class="control-panel">
        <button type="button" class="btn reset-colors material-icons" v-on:click="logout">logout</button>
        <div id="click_catcher">
          <img id="graph" src="/img/graph.svg">
        </div>
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
        <button class="btn confirm-colors material-icons" v-on:click="send">send</button>
        <button class="btn reset-colors material-icons" v-on:click="resetWithClear">backspace</button>
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

          <tbody v-if="points">
          <tr v-for="point in points" :key="point.id">
            <td>{{point.x}}</td>
            <td>{{point.y}}</td>
            <td>{{point.r}}</td>
            <td>{{point.insideArea ? 'да' : 'нет'}}</td>
            <td>{{point.timestamp}}</td>
            <td>{{point.executionTime}}</td>
          </tr>
          </tbody>
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
      points: undefined
    }
  },

  created: async function() {
    console.log('let me grab your ass and check your permissions for this page!!')

    let res = await fetch('https://wf.savok.pw/lab4back-1_0-SNAPSHOT/api/user', {
      credentials: 'include'
    });

    if (!res.ok) await this.$router.push('/')
    else {
      res = await fetch('https://wf.savok.pw/lab4back-1_0-SNAPSHOT/api/points', {
        credentials: 'include'
      });

      if (res.ok) {
        this.points = await res.json();
      } else await this.$router.push('/')
    }
  },

  methods: {
    send: async function() {
      if (this.xValue === "") {
        alert('Неверное значение X')
        return;
        // TODO: maybe we should show this error message at the page?
      }

      const trueY = +this.yValue;
      if (this.yValue === "" || isNaN(trueY) || trueY < -5 || trueY > 3) {
        alert('Неверное значение Y')
        return;
        // TODO: maybe we should show this error message at the page?
      }

      if (this.rValue === "" || +this.rValue < 0) {
        alert('Неверное значение R')
        return;
        // TODO: maybe we should show this error message at the page?
      }

      const res = await fetch('https://wf.savok.pw/lab4back-1_0-SNAPSHOT/api/points', {
        method: 'POST',
        body: new URLSearchParams({
          x: this.xValue,
          y: this.yValue,
          r: this.rValue
        }),
        credentials: 'include'
      })
      if (!res.ok) {
        await this.$router.push("/")
      } else {
        const json = await res.json();
        this.points.push(json);
      }

      this.reset()
    },
    reset: function() {
      this.xValue = "";
      this.yValue = "";
      this.rValue = "";
    },
    resetWithClear: async function() {
      this.reset()

      await fetch('https://wf.savok.pw/lab4back-1_0-SNAPSHOT/api/points', {
        method: 'DELETE',
        credentials: 'include'
      })

      this.points = []
    },
    logout: async function() {
      await fetch('https://wf.savok.pw/lab4back-1_0-SNAPSHOT/api/user', {
        credentials: 'include',
        method: "DELETE"
      });
      document.cookie = "";
      await this.$router.push("/")
    },

    getCoords(elem) {
      let box = elem.getBoundingClientRect();

      return {
        top: box.top + window.pageYOffset,
        right: box.right + window.pageXOffset,
        bottom: box.bottom + window.pageYOffset,
        left: box.left + window.pageXOffset
      };
    },

    drawOMarker(x, y) {
      this.deleteOMarker();
      const svg = document.getElementById('graph');

      let circle = document.createElementNS("http://www.w3.org/2000/svg", 'circle');
      circle.setAttributeNS(null, 'cx', (x * 20 + 150).toString());
      circle.setAttributeNS(null, 'cy', (-y * 20 + 150).toString());
      circle.setAttributeNS(null, 'r', '8');
      circle.setAttributeNS(null, 'stroke', 'rgb(174, 193, 187)');
      circle.setAttributeNS(null, 'stroke-width', '5');
      circle.setAttributeNS(null, 'fill-opacity', '0');
      circle.id = "selected_pos";
      svg.appendChild(circle);
    },

    deleteOMarker() {
      let circle = document.getElementById('selected_pos');
      if (circle !== null) circle.parentElement.removeChild(circle);
    },

    graphMouseMove(evt) {
      let offset = this.getCoords(evt.target);
      const width = offset.right - offset.left;
      const height = offset.bottom - offset.top;

      let x = (evt.pageX - offset.left - width / 2) / (width / 2) * (5 / 2 * 3);
      let y = -Math.round((evt.pageY - offset.top - height / 2) / (height / 2) * (5 / 2 * 3));

      if (x < -3) { x = -3; }
      else if (x > 5) { x = 5; }

      if (y < -5 ) { y = -5; }
      else if ( y > 5) { y = 5; }

      this.drawOMarker(x, y);

      // this.xValue = x;
      // this.yValue = y;
    }
  }
}
</script>

<style scoped>

</style>