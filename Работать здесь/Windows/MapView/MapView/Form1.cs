using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.IO;

namespace MapView
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
            xstart = 0; ystart = 0; zoom = 0; scaled = false;
        }

        private int xstart;
        private int ystart;
        private int zoom;
        private bool scaled;
        private Image nomapImg;
        private Point oldPt;
        /// <summary>
        /// Map folder
        /// </summary>
        private string dir; 

        private void navigateUpToolStripMenuItem_Click(object sender, EventArgs e)
        {
            moveUp();
        }

        private void moveUp()
        {
            if(ystart > 0) ystart--;
            showMap();
            return;
        }
        private void moveDown()
        {
            ystart++;
            showMap();
        }

        private void moveDownToolStripMenuItem_Click(object sender, EventArgs e)
        {
            moveDown();
        }

        private void moveLeftToolStripMenuItem_Click(object sender, EventArgs e)
        {
            moveLeft();
        }

        private void moveLeft()
        {
            if(xstart > 0) xstart--;
            showMap();
        }
        private void moveRight()
        {
            xstart++;
            showMap();
        }

        private void moveRightToolStripMenuItem_Click(object sender, EventArgs e)
        {
            moveRight();
        }


        private void ZoomUp()
        {
            zoom++;
            //restore current position
            xstart = xstart * 2;
            ystart = ystart * 2;
            showMap();

        }

        private void ZoomDown()
        {
            if(zoom > 0) zoom--;
            //restore current position
            xstart = xstart / 2;
            ystart = ystart / 2;
            showMap();
        }

        private void enlargeToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ZoomUp();
        }

        private void mToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ZoomDown();
        }

        private void toStart()
        {
            xstart = ystart = zoom = 0;
            showMap();
        }

        private void gotoStartToolStripMenuItem_Click(object sender, EventArgs e)
        {
            toStart();
        }


        private void openToolStripMenuItem_Click(object sender, EventArgs e)
        {
            FolderBrowserDialog fbd = new FolderBrowserDialog();
            if (fbd.ShowDialog() == DialogResult.OK)
            {
                dir = fbd.SelectedPath;

                toStart();
            }
        }

        private void panelDraw_SizeChanged(object sender, EventArgs e)
        {
            //refresh view
            drawMap();
        }

        private void panelDraw_Paint(object sender, PaintEventArgs e)
        {
            Graphics g = e.Graphics;
            
            if (!scaled)
            {
                Paint256(e.Graphics);//draw normal version of map
                return;
            }
            //draw minimaps
            Image img;

            if (String.IsNullOrEmpty(dir)) toolStripStatusLabelText.Text = "No map files!";
            else
            {
                Size sz = panelDraw.Size;
                int xs = sz.Width / 16;
                int ys = sz.Height / 16;
                //remains x y
                int yss = sz.Height % 16; 
                int xss = sz.Width % 16;
                for (int i = 0; i < ys; i++)
                {
                    for (int j = 0; j < xs; j++)
                    {
                        img = loadImage(j + xstart, i + ystart);
                        g.DrawImage(img, j * 16, i * 16, 16, 16);
                        CloseImage(img);
                    }
                    //draw last horizontal image
                    img = loadImage(xs + xstart, i + ystart);
                    g.DrawImage(img, xs * 16, i * 16, 16, 16);
                    CloseImage(img);
                }
                //here draw last row of map
                for (int k = 0; k < xs; k++)
                {
                    img = loadImage(k + xstart, ys + ystart);
                    g.DrawImage(img, k * 16, ys * 256, 16, 16 );
                    CloseImage(img);
                }
                //draw last horizontal image
                img = loadImage(xs + xstart, ys + ystart);
                g.DrawImage(img, xs * 16, ys * 16, 16, 16);
                CloseImage(img);
                toolStripStatusLabelText.Text = "";
            }
            
        }

        private void CloseImage(Image img)
        {
            if (img != nomapImg)
                img.Dispose();
        }

        private void Paint256(Graphics g)
        {
            Image img;

            if (String.IsNullOrEmpty(dir)) toolStripStatusLabelText.Text = "No map files!";
            else
            {
                Size sz = panelDraw.Size;
                int xs = sz.Width / 256;
                int ys = sz.Height / 256;
                //remains x y
                int yss = sz.Height % 256;
                int xss = sz.Width % 256;
                for (int i = 0; i < ys; i++)
                {
                    for (int j = 0; j < xs; j++)
                    {
                        img = loadImage(j + xstart, i + ystart);
                        g.DrawImageUnscaled(img, j * 256, i * 256);
                    }
                    //draw last horizontal image
                    img = loadImage(xs + xstart, i + ystart);
                    g.DrawImageUnscaled(img, xs * 256, i * 256, xss, 256);
                }
                //here draw last row of map
                for (int k = 0; k < xs; k++)
                {
                    img = loadImage(k + xstart, ys + ystart);
                    g.DrawImageUnscaled(img, k * 256, ys * 256, xss, yss);
                }
                //draw last horizontal image
                img = loadImage(xs + xstart, ys + ystart);
                g.DrawImageUnscaled(img, xs * 256, ys * 256, xss, yss);
                toolStripStatusLabelText.Text = "";
            }

        }

        private Image loadImage(int x, int y)
        {
            string pat = makePath(x, y, zoom);
            if (!File.Exists(pat))
            {
                //use cached nomap image
                if (nomapImg == null)
                {
                    pat = Path.Combine(dir, "nomap.jpg");
                    nomapImg = Image.FromFile(pat);
                }
                return nomapImg;
            }
            else
                return Image.FromFile(pat);
        }


        private string makePath(int x, int y, int z)
        {
            return String.Format("{0}\\{1}\\{2}\\{3}.jpg", dir, z, y, x);
        }


        private void showMap()
        {
            //check position, fix, disable unused menu elements
            checkMenu();
            //load and draw map pies
            drawMap();
            //set up Status bar
            setStatus();
        }

        private void checkMenu()
        {


        }

        private void drawMap()
        {
            panelDraw.Invalidate();
        }

        private void setStatus()
        {
           
            toolStripStatusLabelZ.Text = zoom.ToString();            
            toolStripStatusLabelY.Text = ystart.ToString();
            toolStripStatusLabelX.Text = xstart.ToString();

        }

        private void Form1_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.W)
                moveUp();
            else if (e.KeyCode == Keys.S)
                moveDown();
            else if (e.KeyCode == Keys.A)
                moveLeft();
            else if (e.KeyCode == Keys.D)
                moveRight();
            else if (e.KeyCode == Keys.Q)
                ZoomUp();
            else if (e.KeyCode == Keys.E)
                ZoomDown();
            else if (e.KeyCode == Keys.R)
                toStart();
            else return;
            //postproc keys
            e.Handled = true;
        }

        private void panelDraw_MouseMove(object sender, MouseEventArgs e)
        {
            if (!scaled)
            {//get tile-based mouse coords here
                double x = (double)e.X;
                x = x / 256; //256=image size
                x = x + xstart;
                double y = (double)e.Y;
                y = (y / 256) + ystart;

                PointF pt = TileToWorldPos(x, y, zoom);
                this.toolStripStatusLabel_coordX.Text = pt.X.ToString();
                this.toolStripStatusLabel_coordY.Text = pt.Y.ToString();
            }
            else
            {
                this.toolStripStatusLabel_coordX.Text = "Puzzle mode";
                this.toolStripStatusLabel_coordY.Text = "Puzzle mode";
            }
        }

        private PointF WorldToTilePos(double lon, double lat, int zoom)
        {
            PointF p = new Point();
            p.X = (float)(((lon + 180.0) / 360.0) * (1 << zoom));
            p.Y = (float)((1.0 - Math.Log(Math.Tan(lat * Math.PI / 180.0) +
                1.0 / Math.Cos(lat * Math.PI / 180.0)) / Math.PI) / 2.0 * (1 << zoom));

            return p;
        }

        private PointF TileToWorldPos(double tile_x, double tile_y, int zoom)
        {
            PointF p = new Point();
            double n = Math.PI - ((2.0 * Math.PI * tile_y) / Math.Pow(2.0, zoom));

            p.X = (float)((tile_x / Math.Pow(2.0, zoom) * 360.0) - 180.0);
            p.Y = (float)(180.0 / Math.PI * Math.Atan(0.5 * (Math.Exp(n) - Math.Exp(-n))));

            return p;
        }

        private void toolStripMenuItem1_CheckedChanged(object sender, EventArgs e)
        {
            scaled = toolStripMenuItem1.Checked;
            showMap();
        }

        private void panelDraw_MouseDown(object sender, MouseEventArgs e)
        {
            if(e.Button == MouseButtons.Left)
                oldPt = e.Location;
        }

        private void panelDraw_MouseUp(object sender, MouseEventArgs e)
        {
            if((oldPt.X == 0) || (oldPt.Y == 0)) return;
            
            if (e.Button == MouseButtons.Left)
            {
                int f = 256;
                if (scaled) f = 16;

                int x = e.X - oldPt.X;
                xstart = xstart - x / f;
                if (xstart < 0) xstart = 0;
                int y = e.Y - oldPt.Y;
                ystart = ystart - y / f;
                if (ystart < 0) ystart = 0;
                oldPt.X = 0; oldPt.Y = 0;
                showMap();
            }
        }



    }
}