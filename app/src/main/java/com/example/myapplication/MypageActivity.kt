package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ColorTemplate.COLORFUL_COLORS
import kotlinx.android.synthetic.main.activity_mypage.*
import me.itangqi.waveloadingview.WaveLoadingView


class MypageActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        //test를 위한 seekbar
        var seekbar: SeekBar = findViewById(R.id.seekbar)  //java 에서는 SeekBar seekBar = (SeekBar)findViewById(R.id.seekbar)

        //남은 시간을 나타내는 원 웨이브 이펙트
        var waveLoadongView: WaveLoadingView = findViewById(R.id.waveLoadongView)
        waveLoadongView.setProgressValue(0);

        //seekbar의 동작에 따라 달라지는 웨이브 이팩트 progress가 시간(현재까지 소요된 시간)이고 50분이 max로 설정해둠 남은시간(50-progress)을 표시하도록 설정해둠
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                waveLoadongView.setProgressValue(progress);

                if (progress < 90) {
                    waveLoadongView.setBottomTitle("");
                    waveLoadongView.setCenterTitle(String.format("%d분", (100 - progress) / 2));
                    waveLoadongView.setTopTitle("");
                    waveLoadongView.setWaveColor(Color.parseColor("#8ECAE6"));
                } else {
                    waveLoadongView.setBottomTitle("");
                    waveLoadongView.setCenterTitle(String.format("%d분", (100 - progress) / 2));
                    waveLoadongView.setTopTitle("");
                    waveLoadongView.setWaveColor(Color.parseColor("#FFB703"));
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        var user_num = intent.getStringExtra("user_num")
        var id = intent.getStringExtra("id")
        var pw = intent.getStringExtra("pw")
        var name = intent.getStringExtra("name")
        var dorm_num = intent.getStringExtra("dorm_num")
        var phone_num = intent.getStringExtra("phone_num")
        var using_num = intent.getStringExtra("using_num")

        mypage_name.text = "이름 : " + name
        mypage_id.text = "ID : " + id
        mypage_phone.text = "연락처 : " + phone_num
        mypage_dorm.text = "기숙사 : " + dorm_num

        /*사용자 데이터 분석 그래프*/

        //요일 원형 그래프
        /*수정 필요 : 데이터 data_all, data_my 배열에 INT형으로 넣으면 됨! */
        var Day_of_all = arrayOf(10, 20, 30, 40, 50, 50, 60)    //예시
        var Day_of_my = arrayOf(500, 300, 750, 800, 400, 700, 200)  //예시
        makedaycharts(Day_of_all, Day_of_my)


        //시간 꺾은 선 그래프
        /*아직 미완 */
        var Time_of_all = Array(24,{i ->(0..50).random()})    //시간 x축 데이터
        var Time_of_my = Array<Int>(24, {i->(0..10).random()})  // 사용자 수 y축 데이터 넣어야함 일단 0부터 20중 숫잘 랜덤값으로 초기화함
        maketimecharts(Time_of_all, Time_of_my)

        /*스위치 버튼 이벤트*/
        switchreport.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener
        { buttonView, isChecked ->
            if (isChecked == true) {
                //Toast.makeText(this, "스위치-ON", Toast.LENGTH_SHORT).show()
                chart.setEnabled(true)
                chart.visibility = View.INVISIBLE
                chart2.setEnabled(true)
                chart2.visibility = View.INVISIBLE
                lineChart.visibility = View.VISIBLE
                lineChart.animateX(1800, Easing.EaseInExpo)
            } else {
                //Toast.makeText(this, "스위치-OFF", Toast.LENGTH_SHORT).show()
                chart.setEnabled(false)
                chart.visibility = View.VISIBLE
                chart.animateY(1400, Easing.EaseInOutQuad)
                chart.setEnabled(false)
                chart2.visibility = View.VISIBLE
                chart2.animateY(1400, Easing.EaseInOutQuad)
                lineChart.visibility = View.INVISIBLE
            }
        })
    }

    fun makedaycharts(data_all: Array<Int>, data_my: Array<Int>) :Unit{
        /* 요일 원형 그래프 */
        var Dayofweek = arrayOf("월","화","수","목","금","토","일")
        chart.setUsePercentValues(true)
        chart2.setUsePercentValues(true)

        // 데이터 set
        val chart1_entries = ArrayList<PieEntry>()
        val chart2_entries = ArrayList<PieEntry>()
        for( i in 0..6) {
            chart1_entries.add(PieEntry(data_all[i].toFloat(), Dayofweek[i]))
            chart2_entries.add(PieEntry(data_my[i].toFloat(), Dayofweek[i]))

        }

        // 각 블록 색깔 지정 (꼭 리스트 값안에 담아야 함)
        val colorsItems = ArrayList<Int>()
        colorsItems.add(getResources().getColor(R.color.Reinbow_1))
        colorsItems.add(getResources().getColor(R.color.Reinbow_2))
        colorsItems.add(getResources().getColor(R.color.Reinbow_3))
        colorsItems.add(getResources().getColor(R.color.Reinbow_4))
        colorsItems.add(getResources().getColor(R.color.Reinbow_5))
        colorsItems.add(getResources().getColor(R.color.Reinbow_6))
        colorsItems.add(getResources().getColor(R.color.Reinbow_7))

        //PieDataSet 변수를 만들어 위에서 셋팅한 색상과 그래프에 들어갈 퍼센테이지 수치 색상과 사이즈를 지정할 수 있다.
        //생성할 때, entries는 위에서 데이터셋한 리스트의 이름이고 오른쪽은 value값인데 빈값으로 셋팅해도 된다
        val chart1_pieDataSet = PieDataSet(chart1_entries, "")
        chart1_pieDataSet.apply {
            colors = colorsItems
            valueTextColor = Color.BLACK
            valueTextSize = 16f
        }
        val chart2_pieDataSet = PieDataSet(chart2_entries, "")
        chart2_pieDataSet.apply {
            colors = colorsItems
            valueTextColor = Color.BLACK
            valueTextSize = 16f
        }

        //PieData변수에 위에서 만든 PieDataSet을 넘겨주고, xml에서 만든 chart에 데이터를 셋팅해주는 부분
        val pieData = PieData(chart1_pieDataSet)
        chart.apply {
            data = pieData
            //그래프 이름...보기싫어서 안보이게 해둠..^^
            description.isEnabled = false
            //그래프 만지면 회전하는 애니메이션.. 안쓸꺼임^^
            isRotationEnabled = false
            centerText = "MY"
            setCenterTextSize(20f)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(10f)
            //그래프 최초 시작시 12시방향으로 휘리릭.. 이뿌다....^^
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }
        val pieData2 = PieData(chart2_pieDataSet)
        chart2.apply {
            data = pieData2
            description.isEnabled = false
            isRotationEnabled = false
            centerText = "ALL"
            setCenterTextSize(20f)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(10f)
            animateY(1400, Easing.EaseInOutQuad)
            animate()

        }

        //아래 라벨 숨기기
        val l: Legend = chart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.isEnabled = false

        val l2: Legend = chart2.getLegend()
        l2.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l2.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l2.orientation = Legend.LegendOrientation.VERTICAL
        l2.setDrawInside(false)
        l2.isEnabled = false
    }

    fun maketimecharts(val1List : Array<Int>, val2List : Array<Int> ) :Unit{
        //Part1
        var timeList = Array(24,{i ->i})    //시간(0~23시)
        val entries = ArrayList<Entry>()
        val entries2 = ArrayList<Entry>()

        //Part2
        for(i in 0..23) {
            entries.add(Entry(timeList[i].toFloat(), val1List[i].toFloat()))
            entries2.add(Entry(timeList[i].toFloat(), val2List[i].toFloat()))
        }

        //Part3
        val vl = LineDataSet(entries, "All")
        val v2 = LineDataSet(entries2, "My")

        //Part4
        vl.setDrawValues(false) //각 점의 수 표시하기
        vl.setDrawFilled(true)  //선그래프 아래 색 채우기
        vl.setColor(getResources().getColor(R.color.Reinbow_7))
        vl.setCircleColor(getResources().getColor(R.color.Reinbow_7))
        vl.setFillColor(getResources().getColor(R.color.Reinbow_6))
        vl.lineWidth = 3f

        v2.setDrawValues(false)
        v2.setDrawFilled(true)
        v2.lineWidth = 3f
        v2.setColor(getResources().getColor(R.color.Reinbow_1))
        v2.setCircleColor(getResources().getColor(R.color.Reinbow_1))
        v2.setFillColor(getResources().getColor(R.color.Reinbow_2))


        //Part5
        lineChart.xAxis.labelRotationAngle = 0f

        //Part6
        //두 그래프 합치기
        val chartData = LineData()
        chartData.addDataSet(vl)
        chartData.addDataSet(v2)
        lineChart.data = chartData
        lineChart.invalidate()

        //Part7
        lineChart.axisRight.isEnabled = false


        //Part8
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)

        //Part9
        lineChart.description.text = "Hour"
        lineChart.setNoDataText("아직 데이터가 부족합니다!")



        lineChart.visibility=View.INVISIBLE
    }
}

